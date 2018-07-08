package infrastructure.services;

import infrastructure.RepresentationMetadata;
import infrastructure.SQLUnitOfWork;
import infrastructure.SQLUnitOfWorkFactory;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;

/**
 * An infrastructure service offering several interactions with HTTP representation metadata.
 *
 * @author jonfreer
 * @since 1/4/17
 */
@Service
public class RepresentationMetadataService implements infrastructure.RepresentationMetadataService {

  private final SQLUnitOfWorkFactory unitOfWorkFactory;
  private final Tracer tracer;
  private final Calendar calendar;
  private final Logger logger;

  @Inject
  public RepresentationMetadataService(
      SQLUnitOfWorkFactory unitOfWorkFactory, Tracer tracer, Logger logger) {
    this.unitOfWorkFactory = unitOfWorkFactory;
    this.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    this.tracer = tracer;
    this.logger = logger;
  }

  /**
   * Retrieves representation metadata for a representation with the provided metadata.
   *
   * @param uri The content location (URI) of the representation.
   * @param language The language of the representation.
   * @param encoding The encoding of the representation.
   * @param contentType The media type of the representation.
   * @return The representation metadata for the representation with the provided metadata.
   */
  @Override
  public RepresentationMetadata get(
      URI uri, Locale language, String encoding, MediaType contentType) {
    Span span =
        this.tracer
            .buildSpan("RepresentationMetadataService#get")
            .asChildOf(this.tracer.activeSpan())
            .start();
    final String sql =
        "SELECT RM.CONTENT_LOCATION, RM.CONTENT_TYPE, RM.CONTENT_LANGUAGE, RM.CONTENT_ENCODING, RM.LAST_MODIFIED, RM.ENTITY_TAG FROM REPRESENTATION_METADATA AS RM WHERE RM.CONTENT_LOCATION = ? AND RM.CONTENT_TYPE = ? AND (CONTENT_LANGUAGE IS NULL AND ? IS NULL OR CONTENT_LANGUAGE IS NOT NULL AND CONTENT_LANGUAGE = ?) AND (CONTENT_ENCODING IS NULL AND ? IS NULL OR CONTENT_ENCODING IS NOT NULL AND CONTENT_ENCODING = ?)";
    this.logger.debug(sql);
    RepresentationMetadata representationMetadata = null;
    String lang = null;
    if (language != null) {
      lang = language.toString();
    }

    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
      try (PreparedStatement statement = unitOfWork.createPreparedStatement(sql)) {
        int columnIndex = 0;
        statement.setString(++columnIndex, uri.toString());
        statement.setString(++columnIndex, contentType.toString());
        statement.setString(++columnIndex, lang);
        statement.setString(++columnIndex, lang);
        statement.setString(++columnIndex, encoding);
        statement.setString(++columnIndex, encoding);

        try (ResultSet results = statement.executeQuery()) {
          if (results.next()) {
            columnIndex = 0;
            String matchingUri = results.getString(++columnIndex);
            String contentTypeString = results.getString(++columnIndex);
            String contentLanguageString = results.getString(++columnIndex);
            String contentEncoding = results.getString(++columnIndex);
            Timestamp lastModified = results.getTimestamp(++columnIndex, this.calendar);
            String entityTag = results.getString(++columnIndex);
            entityTag = entityTag.replace("\"", "");

            representationMetadata =
                new RepresentationMetadata(
                    URI.create(matchingUri),
                    MediaType.valueOf(contentTypeString),
                    contentLanguageString == null || contentLanguageString.isEmpty()
                        ? null
                        : new Locale(contentLanguageString),
                    encoding,
                    lastModified,
                    new EntityTag(entityTag));
          }
          unitOfWork.save();
        }
      } catch (SQLException x) {
        unitOfWork.undo();
        throw new RuntimeException(x);
      }
    } finally {
      span.finish();
    }

    return representationMetadata;
  }

  /**
   * Retrieves all representation metadata associated with the provided content location (URI).
   *
   * @param uri The content location (URI) of the representation metadata.
   * @return All representation metdata that is associated with the provided content location (URI).
   */
  public List<RepresentationMetadata> getAll(URI uri) {
    Span span =
        this.tracer
            .buildSpan("RepresentationMetadataService#getAll")
            .asChildOf(this.tracer.activeSpan())
            .start();
    final String sql =
        "SELECT RM.CONTENT_LOCATION, RM.CONTENT_TYPE, RM.CONTENT_LANGUAGE, RM.CONTENT_ENCODING, RM.LAST_MODIFIED, RM.ENTITY_TAG FROM REPRESENTATION_METADATA AS RM WHERE RM.CONTENT_LOCATION = ?;";
    this.logger.debug(sql);
    List<RepresentationMetadata> representationMetadata = new ArrayList<>();

    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
      try (PreparedStatement statement = unitOfWork.createPreparedStatement(sql)) {
        int columnIndex = 0;
        statement.setString(++columnIndex, uri.toString());
        try (ResultSet results = statement.executeQuery()) {
          while (results.next()) {
            columnIndex = 0;
            String matchingUri = results.getString(++columnIndex);
            String contentTypeString = results.getString(++columnIndex);
            String contentLanguageString = results.getString(++columnIndex);
            String contentEncoding = results.getString(++columnIndex);
            Timestamp lastModified = results.getTimestamp(++columnIndex, this.calendar);
            String entityTag = results.getString(++columnIndex);
            entityTag = entityTag.replace("\"", "");

            representationMetadata.add(
                new RepresentationMetadata(
                    URI.create(matchingUri),
                    MediaType.valueOf(contentTypeString),
                    new Locale(contentLanguageString),
                    contentEncoding,
                    lastModified,
                    new EntityTag(entityTag)));
          }
          unitOfWork.save();
        }
      } catch (SQLException x) {
        unitOfWork.undo();
        throw new RuntimeException(x);
      }
    } finally {
      span.finish();
    }

    return representationMetadata;
  }

  /**
   * Creates representation metadata with the provided state.
   *
   * @param representationMetadata The desired state for the new resource metadata.
   */
  @Override
  public void insert(RepresentationMetadata representationMetadata) {
    Span span =
        this.tracer
            .buildSpan("RepresentationMetadataService#insert")
            .asChildOf(this.tracer.activeSpan())
            .start();
    final String sql =
        "INSERT INTO REPRESENTATION_METADATA (UUID, CONTENT_LOCATION, CONTENT_LOCATION_HASH, CONTENT_TYPE, CONTENT_LANGUAGE, CONTENT_ENCODING, LAST_MODIFIED, ENTITY_TAG) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
    this.logger.debug(sql);
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
      try (PreparedStatement statement = unitOfWork.createPreparedStatement(sql)) {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] bytesMD5 =
            digest.digest(
                representationMetadata
                    .getContentLocation()
                    .toString()
                    .getBytes(Charset.forName("UTF-8")));

        int columnIndex = 0;
        statement.setString(++columnIndex, UUID.randomUUID().toString());
        statement.setString(++columnIndex, representationMetadata.getContentLocation().toString());
        statement.setString(++columnIndex, new String(bytesMD5));
        statement.setString(++columnIndex, representationMetadata.getContentType().toString());
        if (representationMetadata.getContentLanguage() == null) {
          statement.setNull(++columnIndex, Types.VARCHAR);
        } else {
          statement.setString(
              ++columnIndex, representationMetadata.getContentLanguage().toString());
        }
        statement.setString(++columnIndex, representationMetadata.getContentEncoding());
        statement.setTimestamp(
            ++columnIndex,
            new Timestamp(representationMetadata.getLastModified().getTime()),
            this.calendar);
        statement.setString(++columnIndex, representationMetadata.getEntityTag().toString());
        statement.executeUpdate();
        unitOfWork.save();
      } catch (SQLException | NoSuchAlgorithmException x) {
        unitOfWork.undo();
        throw new RuntimeException(x);
      }
    } finally {
      span.finish();
    }
  }

  /**
   * Replaces the state an existing representation of metadata about a resource with the provided
   * state.
   *
   * @param representationMetadata The desired state for the resource metadata.
   */
  @Override
  public void put(RepresentationMetadata representationMetadata) {
    Span span =
        this.tracer
            .buildSpan("RepresentationMetadataService#put")
            .asChildOf(this.tracer.activeSpan())
            .start();
    if (this.get(
            representationMetadata.getContentLocation(),
            representationMetadata.getContentLanguage(),
            representationMetadata.getContentEncoding(),
            representationMetadata.getContentType())
        == null) {
      this.insert(representationMetadata);
    } else {

      StringBuilder sqlStringBuilder = new StringBuilder();
      final String sql =
          sqlStringBuilder
              .append("UPDATE")
              .append("\n\t")
              .append("REPRESENTATION_METADATA")
              .append("\n")
              .append("SET")
              .append("\n\t")
              .append("LAST_MODIFIED = ?,")
              .append("\n\t")
              .append("ENTITY_TAG = ?")
              .append("\n")
              .append("WHERE")
              .append("\n\t")
              .append("CONTENT_LOCATION = ? AND")
              .append("\n\t")
              .append("CONTENT_TYPE = ? AND")
              .append("\n\t")
              .append(
                  "(CONTENT_LANGUAGE IS NULL AND ? IS NULL OR CONTENT_LANGUAGE IS NOT NULL AND CONTENT_LANGUAGE = ?) AND")
              .append("\n\t")
              .append(
                  "(CONTENT_ENCODING IS NULL AND ? IS NULL OR CONTENT_ENCODING IS NOT NULL AND CONTENT_ENCODING = ?);")
              .toString();
      this.logger.debug(sql);

      try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
        SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

        String contentLanguage = null;
        if (representationMetadata.getContentLanguage() != null) {
          contentLanguage = representationMetadata.getContentLanguage().toString();
        }

        try (PreparedStatement statement = unitOfWork.createPreparedStatement(sql)) {
          int columnIndex = 0;
          statement.setTimestamp(
              ++columnIndex,
              new Timestamp(representationMetadata.getLastModified().getTime()),
              this.calendar);
          statement.setString(++columnIndex, representationMetadata.getEntityTag().toString());
          statement.setString(
              ++columnIndex, representationMetadata.getContentLocation().toString());
          statement.setString(++columnIndex, representationMetadata.getContentType().toString());
          statement.setString(++columnIndex, contentLanguage);
          statement.setString(++columnIndex, contentLanguage);
          statement.setString(++columnIndex, representationMetadata.getContentEncoding());
          statement.setString(++columnIndex, representationMetadata.getContentEncoding());
          statement.executeUpdate();
          unitOfWork.save();
        } catch (SQLException x) {
          unitOfWork.undo();
          throw new RuntimeException(x);
        }
      } finally {
        span.finish();
      }
    }
  }

  /**
   * Deletes the resource metadata for a resource.
   *
   * @param uri The URI of the resource to delete metadata for.
   */
  @Override
  public void remove(URI uri, Locale language, String encoding, MediaType contentType) {
    Span span =
        this.tracer
            .buildSpan("RepresentationMetadataService#remove")
            .asChildOf(this.tracer.activeSpan())
            .start();
    final String sql =
        "DELETE FROM REPRESENTATION_METADATA WHERE CONTENT_LOCATION = ? AND CONTENT_TYPE = ? AND CONTENT_LANGUAGE = ? AND CONTENT_ENCODING = ?;";
    this.logger.debug(sql);

    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
      try (PreparedStatement statement = unitOfWork.createPreparedStatement(sql)) {
        int columnIndex = 0;
        statement.setString(++columnIndex, uri.toString());
        statement.setString(++columnIndex, contentType.toString());
        statement.setString(++columnIndex, language.toString());
        statement.setString(++columnIndex, encoding);
        statement.executeUpdate();
        unitOfWork.save();
      } catch (SQLException x) {
        unitOfWork.undo();
        throw new RuntimeException(x);
      }
    } finally {
      span.finish();
    }
  }

  /**
   * Deletes the resource metadata for a resource.
   *
   * @param uri The URI of the resource to delete metadata for.
   */
  @Override
  public void removeAll(URI uri) {
    Span span =
        this.tracer
            .buildSpan("RepresentationMetadataService#removeAll")
            .asChildOf(this.tracer.activeSpan())
            .start();
    final String sql = "DELETE FROM REPRESENTATION_METADATA WHERE CONTENT_LOCATION = ?;";
    this.logger.debug(sql);

    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
      try (PreparedStatement statement = unitOfWork.createPreparedStatement(sql)) {
        int columnIndex = 0;
        statement.setString(++columnIndex, uri.toString());
        statement.executeUpdate();
        unitOfWork.save();
      } catch (SQLException x) {
        unitOfWork.undo();
        throw new RuntimeException(x);
      }
    } finally {
      span.finish();
    }
  }

  @Override
  public List<RepresentationMetadata> match(
      URI contentLocation,
      List<MediaType> mediaTypes,
      List<Locale> languages,
      List<String> encodings) {
    Span span =
        this.tracer
            .buildSpan("RepresentationMetadataService#match")
            .asChildOf(this.tracer.activeSpan())
            .start();

    final List<RepresentationMetadata> representationMetadata = new ArrayList<>();
    final String tab = "\t";
    final String newLine = "\n";
    final String tabNewLine = tab + newLine;
    final String newLineTab = newLine + tab;
    final StringBuilder sqlBuilder =
        new StringBuilder()
            .append("SELECT")
            .append(newLineTab)
            .append("RM.CONTENT_LOCATION,")
            .append(newLineTab)
            .append("RM.CONTENT_TYPE,")
            .append(newLineTab)
            .append("RM.CONTENT_LANGUAGE,")
            .append(newLineTab)
            .append("RM.CONTENT_ENCODING,")
            .append(newLineTab)
            .append("RM.LAST_MODIFIED,")
            .append(newLineTab)
            .append("RM.ENTITY_TAG")
            .append(newLine)
            .append("FROM")
            .append(newLineTab)
            .append("REPRESENTATION_METADATA AS RM")
            .append(newLine)
            .append("WHERE")
            .append(newLineTab)
            .append("RM.CONTENT_LOCATION = ?")
            .append(newLineTab);

    int numOfMediaTypes = mediaTypes.size();
    if (numOfMediaTypes > 0) {
      sqlBuilder.append(newLineTab).append("AND RM.CONTENT_TYPE IN (");
      for (int i = 0; i < numOfMediaTypes; i++) {
        sqlBuilder.append("?");
        if (i != numOfMediaTypes - 1) {
          sqlBuilder.append(",");
        }
      }
      sqlBuilder.append(")").append(newLineTab);
    }

    int numOfLanguages = languages.size();
    if (numOfLanguages > 0) {
      sqlBuilder.append(newLineTab).append("AND RM.CONTENT_LANGUAGE IN (");
      for (int i = 0; i < numOfLanguages; i++) {
        sqlBuilder.append("?");
        if (i != numOfLanguages - 1) {
          sqlBuilder.append(",");
        }
      }
      sqlBuilder.append(")").append(newLineTab);
    }

    int numOfEncodings = encodings.size();
    if (numOfEncodings > 0) {
      sqlBuilder.append(newLineTab).append("AND RM.CONTENT_ENCODING IN (");
      for (int i = 0; i < numOfEncodings; i++) {
        sqlBuilder.append("?");
        if (i != numOfEncodings - 1) {
          sqlBuilder.append(",");
        }
      }
      sqlBuilder.append(")");
    }
    sqlBuilder.append(";");
    final String sql = sqlBuilder.toString();
    this.logger.debug(sql);

    SQLUnitOfWork unitOfWork = null;

    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      unitOfWork = this.unitOfWorkFactory.create();

      try (PreparedStatement statement = unitOfWork.createPreparedStatement(sql)) {
        int paramIndex = 0;

        statement.setString(++paramIndex, contentLocation.toString());
        for (int m = 0; m < mediaTypes.size(); m++) {
          statement.setString(++paramIndex, mediaTypes.get(m).toString());
        }

        for (int l = 0; l < languages.size(); l++) {
          statement.setString(++paramIndex, languages.get(l).toString());
        }

        for (int e = 0; e < encodings.size(); e++) {
          statement.setString(++paramIndex, encodings.get(e).toString());
        }

        // could make a factory for this part - already need a
        // factory for RepresentationMetadata.
        try (ResultSet results = statement.executeQuery()) {
          int columnIndex = 0;
          while (results.next()) {
            columnIndex = 0;
            String matchingUri = results.getString(++columnIndex);
            String contentTypeString = results.getString(++columnIndex);
            String contentLanguageString = results.getString(++columnIndex);
            String contentEncoding = results.getString(++columnIndex);
            Timestamp lastModified = results.getTimestamp(++columnIndex, this.calendar);
            String entityTag = results.getString(++columnIndex);
            entityTag = entityTag.replace("\"", "");

            representationMetadata.add(
                new RepresentationMetadata(
                    URI.create(matchingUri),
                    MediaType.valueOf(contentTypeString),
                    new Locale(contentLanguageString),
                    contentEncoding,
                    lastModified,
                    new EntityTag(entityTag)));
          }
        }

        unitOfWork.save();
        return representationMetadata;
      }
    } catch (Exception x) {
      unitOfWork.undo();
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }
}
