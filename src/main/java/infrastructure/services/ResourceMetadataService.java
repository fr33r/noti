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

import javax.inject.Inject;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;

import org.jvnet.hk2.annotations.Service;

import infrastructure.SQLUnitOfWork;
import infrastructure.SQLUnitOfWorkFactory;
import infrastructure.ResourceMetadata;

/**
 * An infrastructure service offering several operations to 
 * calling clients that wish to interact with REST resource metadata.
 * 
 * @author jonfreer
 * @since 1/4/17
 */
@Service
public class ResourceMetadataService implements infrastructure.ResourceMetadataService {

	private final SQLUnitOfWorkFactory unitOfWorkFactory;

	@Inject
	public ResourceMetadataService(SQLUnitOfWorkFactory unitOfWorkFactory){
		this.unitOfWorkFactory = unitOfWorkFactory;
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
	public ResourceMetadata get(URI uri, MediaType contentType) {

		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
		final String sql =
			"SELECT RM.URI, RM.CONTENT_TYPE, RM.NODE_NAME, RM.REVISION, RM.LAST_MODIFIED, RM.ENTITY_TAG FROM RESOURCE_METADATA AS RM WHERE RM.URI = ? AND RM.CONTENT_TYPE = ?";
		ResourceMetadata resourceMetadata = null;

		try (PreparedStatement statement = unitOfWork.createPreparedStatement(sql)){
			statement.setString(1, uri.toString());
			statement.setString(2, contentType.toString());
			try(ResultSet results = statement.executeQuery()){
				if(results.next()){
					String matchingUri = results.getString(1);
					String contentTypeString = results.getString(2);
					String nodeName = results.getString(3);
					Long revision = results.getLong(4);
					Timestamp lastModified =
						results.getTimestamp(5, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
					String entityTag = results.getString(6);
					entityTag = entityTag.replace("\"", "");

					resourceMetadata =
						new ResourceMetadata(
							URI.create(matchingUri),
							MediaType.valueOf(contentTypeString),
							nodeName,
							revision,
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

		return resourceMetadata;
	}

	public List<ResourceMetadata> getAll(URI uri) {

		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
		final String sql =
			"SELECT RM.URI, RM.CONTENT_TYPE, RM.NODE_NAME, RM.REVISION, RM.LAST_MODIFIED, RM.ENTITY_TAG FROM RESOURCE_METADATA AS RM WHERE RM.URI = ?;";
		List<ResourceMetadata> resourceMetadata = new ArrayList<>();

		try (PreparedStatement statement = unitOfWork.createPreparedStatement(sql)){
			statement.setString(1, uri.toString());
			try(ResultSet results = statement.executeQuery()){
				while(results.next()){
					String matchingUri = results.getString(1);
					String contentTypeString = results.getString(2);
					String nodeName = results.getString(3);
					Long revision = results.getLong(4);
					Timestamp lastModified =
						results.getTimestamp(5, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
					String entityTag = results.getString(6);
					entityTag = entityTag.replace("\"", "");

					resourceMetadata.add(
						new ResourceMetadata(
							URI.create(matchingUri),
							MediaType.valueOf(contentTypeString),
							nodeName,
							revision,
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

		return resourceMetadata;
	}

	/**
     * Creates a new representation of resource metadata with the
     * provided state.
     *
     * @param resourceMetadata The desired state for the new resource metadata.
     */
	@Override
	public void insert(ResourceMetadata resourceMetadata) {

		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
		final String sql = "INSERT INTO RESOURCE_METADATA (URI, URI_HASH, CONTENT_TYPE, NODE_NAME, REVISION, LAST_MODIFIED, ENTITY_TAG) VALUES (?, ?, ?, ?, ?, ?, ?);";
		try (PreparedStatement statement = unitOfWork.createPreparedStatement(sql)){
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytesMD5 =
				digest.digest(resourceMetadata.getUri().toString().getBytes(Charset.forName("UTF-8")));

			statement.setString(1, resourceMetadata.getUri().toString());
			statement.setString(2, new String(bytesMD5));
			statement.setString(3, resourceMetadata.getContentType().toString());
			statement.setString(4, resourceMetadata.getNodeName());
			statement.setLong(5, resourceMetadata.getRevision());
			statement.setTimestamp(
				6, 
				new Timestamp(resourceMetadata.getLastModified().getTime()),
				Calendar.getInstance(TimeZone.getTimeZone("UTC"))
			);
			statement.setString(7, resourceMetadata.getEntityTag().toString());
			statement.executeUpdate();
			unitOfWork.save();
		} catch (SQLException | NoSuchAlgorithmException x) {
			//should log instead.
			x.printStackTrace();
			unitOfWork.undo();
		}
	}

	/**
     * Replaces the state an existing representation of metadata about a resource
     * with the provided state.
     *
     * @param resourceMetadata The desired state for the resource metadata.
     */
	@Override
	public void put(ResourceMetadata resourceMetadata) {

		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
		final String sql =
			"UPDATE RESOURCE_METADATA SET NODE_NAME = ?, REVISION = ?, LAST_MODIFIED = ?, ENTITY_TAG = ? WHERE URI = ? AND CONTENT_TYPE = ?;";

		try (PreparedStatement statement = unitOfWork.createPreparedStatement(sql)){
			statement.setString(1, resourceMetadata.getNodeName());
			statement.setLong(2, resourceMetadata.getRevision());
			statement.setTimestamp(
				3,
				new Timestamp(resourceMetadata.getLastModified().getTime()),
				Calendar.getInstance(TimeZone.getTimeZone("UTC"))
			);
			statement.setString(4, resourceMetadata.getEntityTag().toString());
			statement.setString(5, resourceMetadata.getUri().toString());
			statement.setString(6, resourceMetadata.getContentType().toString());
			statement.executeUpdate();
			unitOfWork.save();
		} catch (SQLException x) {
			//should log instead.
			x.printStackTrace();
			unitOfWork.undo();
		}
	}

	/**
	 * Deletes the resource metadata for a resource.
	 *
	 * @param uri The URI of the resource to delete metadata for.
	 */
	@Override
	public void remove(URI uri, MediaType contentType) {

		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
		final String sql = "DELETE FROM RESOURCE_METADATA WHERE URI = ? AND CONTENT_TYPE = ?;";

		try(PreparedStatement statement = unitOfWork.createPreparedStatement(sql)){
			statement.setString(1, uri.toString());
			statement.setString(2, contentType.toString());
			statement.executeUpdate();
			unitOfWork.save();
		}catch (SQLException x) {
			//should log instead.
			x.printStackTrace();
			unitOfWork.undo();
		}
	}

	/**
	 * Deletes the resource metadata for a resource.
	 *
	 * @param uri The URI of the resource to delete metadata for.
	 */
	@Override
	public void removeAll(URI uri) {

		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
		final String sql = "DELETE FROM RESOURCE_METADATA WHERE URI = ?;";

		try(PreparedStatement statement = unitOfWork.createPreparedStatement(sql)){
			statement.setString(1, uri.toString());
			statement.executeUpdate();
			unitOfWork.save();
		}catch (SQLException x) {
			//should log instead.
			x.printStackTrace();
			unitOfWork.undo();
		}
	}
}
