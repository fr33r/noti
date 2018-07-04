package infrastructure.services;

import java.net.URI;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Locale;

import javax.inject.Inject;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;

import org.jvnet.hk2.annotations.Service;

import infrastructure.SQLUnitOfWork;
import infrastructure.SQLUnitOfWorkFactory;
import infrastructure.RepresentationMetadata;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

/**
 * An infrastructure service offering several operations to 
 * calling clients that wish to interact with REST resource metadata.
 * 
 * @author jonfreer
 * @since 1/4/17
 */
@Service
public class RepresentationMetadataService implements infrastructure.RepresentationMetadataService {

	private final SQLUnitOfWorkFactory unitOfWorkFactory;
	private final Tracer tracer;
	private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

	@Inject
	public RepresentationMetadataService(
		SQLUnitOfWorkFactory unitOfWorkFactory,
		Tracer tracer
	){
		this.unitOfWorkFactory = unitOfWorkFactory;
		this.tracer = tracer;
	}

	/**
     * Retrieves resource metadata for a resource identified by
     * the provided URI.
     *
     * @param uri The URI of the resource to retrieve metadata for.
     * @return The resource metadata for the resource identified by
     * the provided URI.
     */
	@Override
	public RepresentationMetadata get(URI uri, Locale language, String encoding, MediaType contentType) {
		Span span =
			this.tracer.buildSpan("RepresentationMetadataService#get").asChildOf(this.tracer.activeSpan()).start();
		final String sql =
			"SELECT RM.URI, RM.CONTENT_TYPE, RM.CONTENT_LANGUAGE, RM.CONTENT_ENCODING, RM.LAST_MODIFIED, RM.ENTITY_TAG FROM RESOURCE_METADATA AS RM WHERE RM.URI = ? AND RM.CONTENT_TYPE = ?";
		RepresentationMetadata representationMetadata = null;

		try (Scope scope = this.tracer.scopeManager().activate(span, false)){
			SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
			try (PreparedStatement statement = unitOfWork.createPreparedStatement(sql)) {
				int columnIndex = 0;
				statement.setString(columnIndex++, uri.toString());
				statement.setString(columnIndex++, language.toString());
				statement.setString(columnIndex++, encoding);
				statement.setString(columnIndex++, contentType.toString());
				try(ResultSet results = statement.executeQuery()){
					if(results.next()){
						columnIndex = 0;
						String matchingUri = results.getString(columnIndex++);
						String contentTypeString = results.getString(columnIndex++);
						String contentLanguageString  = results.getString(columnIndex++);
						String contentEncoding = results.getString(columnIndex++);
						Timestamp lastModified =
							results.getTimestamp(columnIndex++, this.calendar);
						String entityTag = results.getString(columnIndex++);
						entityTag = entityTag.replace("\"", "");

						representationMetadata =
							new RepresentationMetadata(
								URI.create(matchingUri),
								MediaType.valueOf(contentTypeString),
								new Locale(contentLanguageString),
								encoding,
								lastModified,
								new EntityTag(entityTag)
							);
					}
					unitOfWork.save();
				}
			} catch (SQLException x) {
				//should log instead.
				x.printStackTrace();
				unitOfWork.undo();
			}
		} finally {
			span.finish();
		}

		return representationMetadata;
	}

	public List<RepresentationMetadata> getAll(URI uri) {
		Span span =
			this.tracer.buildSpan("RepresentationMetadataService#getAll").asChildOf(this.tracer.activeSpan()).start();
		final String sql =
			"SELECT RM.URI, RM.CONTENT_TYPE, RM.CONTENT_LANGUAGE, RM.CONTENT_ENCODING, RM.LAST_MODIFIED, RM.ENTITY_TAG FROM RESOURCE_METADATA AS RM WHERE RM.URI = ?;";
		List<RepresentationMetadata> representationMetadata = new ArrayList<>();

		try (Scope scope = this.tracer.scopeManager().activate(span, false)){
			SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
			try(PreparedStatement statement = unitOfWork.createPreparedStatement(sql)){
				int columnIndex = 0;
				statement.setString(columnIndex++, uri.toString());
				try(ResultSet results = statement.executeQuery()){
					while(results.next()){
						columnIndex = 0;
						String matchingUri = results.getString(columnIndex++);
						String contentTypeString = results.getString(columnIndex++);
						String contentLanguageString = results.getString(columnIndex++);
						String contentEncoding = results.getString(columnIndex++);
						Timestamp lastModified =
							results.getTimestamp(columnIndex++, this.calendar);
						String entityTag = results.getString(columnIndex++);
						entityTag = entityTag.replace("\"", "");

						representationMetadata.add(
							new RepresentationMetadata(
								URI.create(matchingUri),
								MediaType.valueOf(contentTypeString),
								new Locale(contentLanguageString),
								contentEncoding,
								lastModified,
								new EntityTag(entityTag)
							)
						);
					}
					unitOfWork.save();
				}
			} catch (SQLException x) {
				//should log instead.
				x.printStackTrace();
				unitOfWork.undo();
			}
		} finally {
			span.finish();
		}

		return representationMetadata;
	}

	/**
     * Creates a new representation of resource metadata with the
     * provided state.
     *
     * @param representationMetadata The desired state for the new resource metadata.
     */
	@Override
	public void insert(RepresentationMetadata representationMetadata) {
		Span span =
			this.tracer.buildSpan("RepresentationMetadataService#insert").asChildOf(this.tracer.activeSpan()).start();
		final String sql = "INSERT INTO RESOURCE_METADATA (URI, URI_HASH, CONTENT_TYPE, CONTENT_LANGUAGE, CONTENT_ENCODING, LAST_MODIFIED, ENTITY_TAG) VALUES (?, ?, ?, ?, ?, ?, ?);";
		try (Scope scope = this.tracer.scopeManager().activate(span, false)){
			SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
			try(PreparedStatement statement = unitOfWork.createPreparedStatement(sql)) {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				byte[] bytesMD5 =
					digest.digest(representationMetadata.getContentLocation().toString().getBytes(Charset.forName("UTF-8")));

				int columnIndex = 0;
				statement.setString(columnIndex++, representationMetadata.getContentLocation().toString());
				statement.setString(columnIndex++, new String(bytesMD5));
				statement.setString(columnIndex++, representationMetadata.getContentType().toString());
				statement.setString(columnIndex++, representationMetadata.getContentLanguage().toString());
				statement.setString(columnIndex++, representationMetadata.getContentEncoding());
				statement.setTimestamp(
					columnIndex++, 
					new Timestamp(representationMetadata.getLastModified().getTime()),
					this.calendar
				);
				statement.setString(columnIndex++, representationMetadata.getEntityTag().toString());
				statement.executeUpdate();
				unitOfWork.save();
			} catch (SQLException | NoSuchAlgorithmException x) {
				//should log instead.
				x.printStackTrace();
				unitOfWork.undo();
			}
		} finally {
			span.finish();
		}
	}

	/**
     * Replaces the state an existing representation of metadata about a resource
     * with the provided state.
     *
     * @param representationMetadata The desired state for the resource metadata.
     */
	@Override
	public void put(RepresentationMetadata representationMetadata) {
		Span span =
			this.tracer.buildSpan("RepresentationMetadataService#put").asChildOf(this.tracer.activeSpan()).start();
		StringBuilder sqlStringBuilder = new StringBuilder();
		final String sql = sqlStringBuilder
			.append("UPDATE").append("\n\t")
			.append("RESOURCE_METADATA").append("\n")
			.append("SET").append("\n\t")
			.append("CONTENT_LANGUAGE = ?,").append("\n\t")
			.append("CONTENT_ENCODING = ?,").append("\n\t")
			.append("LAST_MODIFIED = ?,").append("\n\t")
			.append("ENTITY_TAG = ?").append("\n")
			.append("WHERE").append("\n\t")
			.append("URI = ? AND").append("\n\t")
			.append("CONTENT_TYPE = ? AND").append("\n\t")
			.append("CONTENT_LANGUAGE = ? AND").append("\n\t")
			.append("CONTENT_ENCODING = ?;")
			.toString();

		try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
			SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
			try(PreparedStatement statement = unitOfWork.createPreparedStatement(sql)) {
				int columnIndex = 0;
				statement.setString(columnIndex++, representationMetadata.getContentLanguage().toString());
				statement.setString(columnIndex++, representationMetadata.getContentEncoding());
				statement.setTimestamp(
					columnIndex++,
					new Timestamp(representationMetadata.getLastModified().getTime()),
					this.calendar
				);
				statement.setString(columnIndex++, representationMetadata.getEntityTag().toString());
				statement.setString(columnIndex++, representationMetadata.getContentLocation().toString());
				statement.setString(columnIndex++, representationMetadata.getContentType().toString());
				statement.executeUpdate();
				unitOfWork.save();
			} catch (SQLException x) {
				//should log instead.
				x.printStackTrace();
				unitOfWork.undo();
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
	public void remove(URI uri, Locale language, String encoding, MediaType contentType) {
		Span span =
			this.tracer.buildSpan("RepresentationMetadataService#remove").asChildOf(this.tracer.activeSpan()).start();
		final String sql = "DELETE FROM RESOURCE_METADATA WHERE URI = ? AND CONTENT_TYPE = ? AND CONTENT_LANGUAGE = ? AND CONTENT_ENCODING = ?;";

		try(Scope scope = this.tracer.scopeManager().activate(span, false)){
			SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
			try(PreparedStatement statement = unitOfWork.createPreparedStatement(sql)) {
				int columnIndex = 0;
				statement.setString(columnIndex++, uri.toString());
				statement.setString(columnIndex++, contentType.toString());
				statement.setString(columnIndex++, language.toString());
				statement.setString(columnIndex++, encoding);
				statement.executeUpdate();
				unitOfWork.save();
			}catch (SQLException x) {
				//should log instead.
				x.printStackTrace();
				unitOfWork.undo();
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
			this.tracer.buildSpan("RepresentationMetadataService#removeAll").asChildOf(this.tracer.activeSpan()).start();
		final String sql = "DELETE FROM RESOURCE_METADATA WHERE URI = ?;";

		try(Scope scope = this.tracer.scopeManager().activate(span, false)){
			SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
			try(PreparedStatement statement = unitOfWork.createPreparedStatement(sql)) {
				int columnIndex = 0;
				statement.setString(columnIndex++, uri.toString());
				statement.executeUpdate();
				unitOfWork.save();
			}catch (SQLException x) {
				//should log instead.
				x.printStackTrace();
				unitOfWork.undo();
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
		List<String> encodings
	) {
		Span span =
			this.tracer
				.buildSpan("RepresentationMetadataService#match")
				.asChildOf(this.tracer.activeSpan())
				.start();
			
		final List<RepresentationMetadata> representationMetadata =
			new ArrayList<>();
		final String tab = "\t";
		final String newLine = "\n";
		final String tabNewLine = tab + newLine;
		final String newLineTab = newLine + tab;
		final StringBuilder sqlBuilder =
			new StringBuilder()
				.append("SELECT").append(newLineTab)
				.append("RM.URI,").append(newLineTab)
				.append("RM.CONTENT_TYPE").append(newLineTab)
				.append("RM.CONTENT_LANGUAGE").append(newLineTab)
				.append("RM.CONTENT_ENCODING").append(newLineTab)
				.append("RM.LAST_MODIFIED").append(newLineTab)
				.append("RM.ENTITY_TAG").append(newLine)
				.append("FROM").append(newLineTab)
				.append("RESOURCE_METADATA AS RM").append(newLine)
				.append("WHERE").append(newLineTab)
				.append("RM.URI = ?").append(newLineTab);

		int numOfMediaTypes = mediaTypes.size();
		if(numOfMediaTypes > 0) {
			sqlBuilder.append(newLineTab).append("AND RM.CONTENT_TYPE IN (");
			for(int i = 0; i < numOfMediaTypes; i++) {
				sqlBuilder.append("?");
				if(i != numOfMediaTypes - 1) {
					sqlBuilder.append(",");
				}
			}
			sqlBuilder.append(newLineTab).append(")");
		}

		int numOfLanguages = languages.size();
		if(numOfLanguages > 0) {
			sqlBuilder.append(newLineTab).append("AND RM.CONTENT_LANGUAGE IN (");
			for(int i = 0; i < numOfLanguages; i++) {
				sqlBuilder.append("?");
				if(i != numOfLanguages - 1) {
					sqlBuilder.append(",");
				}
			}
			sqlBuilder.append(newLineTab).append(")");
		}

		int numOfEncodings = encodings.size();
		if(numOfEncodings > 0) {
			sqlBuilder.append(newLineTab).append("AND RM.CONTENT_ENCODING IN (");
			for(int i = 0; i < numOfEncodings; i++) {
				sqlBuilder.append("?");
				if(i != numOfEncodings - 1) {
					sqlBuilder.append(",");
				}
			}
			sqlBuilder.append(newLineTab).append(")");
		}
		sqlBuilder.append(";");
		final String sql = sqlBuilder.toString();

		SQLUnitOfWork unitOfWork = null;

		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {

			unitOfWork = this.unitOfWorkFactory.create();

			try(
				PreparedStatement statement = 
					unitOfWork.createPreparedStatement(sql)
			) {
				int paramIndex = 0;
				
				statement.setString(paramIndex++, contentLocation.toString());
				for(int m = 0; m < mediaTypes.size(); m++) {
					statement.setString(paramIndex++, mediaTypes.get(m).toString());
				}

				for(int l = 0; l < languages.size(); l++) {
					statement.setString(paramIndex++, languages.get(l).toString());
				}

				for(int e = 0; e < encodings.size(); e++) {
					statement.setString(paramIndex++, encodings.get(e).toString());
				}

				//could make a factory for this part - already need a
				//factory for RepresentationMetadata.
				try(ResultSet results = statement.executeQuery()) {
					int columnIndex = 0;
					while(results.next()) {
						columnIndex = 0;
						String matchingUri = results.getString(columnIndex++);
						String contentTypeString = results.getString(columnIndex++);
						String contentLanguageString = results.getString(columnIndex++);
						String contentEncoding = results.getString(columnIndex++);
						Timestamp lastModified =
							results.getTimestamp(columnIndex++, this.calendar);
						String entityTag = results.getString(columnIndex++);
						entityTag = entityTag.replace("\"", "");

						representationMetadata.add(
							new RepresentationMetadata(
								URI.create(matchingUri),
								MediaType.valueOf(contentTypeString),
								new Locale(contentLanguageString),
								contentEncoding,
								lastModified,
								new EntityTag(entityTag)
							)
						);
					}
				}

				unitOfWork.save();
				return representationMetadata;
			} 
		} catch(Exception x) {
			//log.
			unitOfWork.undo();
		} finally {
			span.finish();
		}
		return representationMetadata;
	}
}
