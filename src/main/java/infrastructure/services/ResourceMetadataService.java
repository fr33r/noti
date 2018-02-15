package infrastructure.services;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
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
	public ResourceMetadata getResourceMetadata(URI uri, MediaType contentType) {

		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
		PreparedStatement statement =
			unitOfWork.createPreparedStatement(
				"SELECT RM.* FROM RESOURCE_METADATA AS RM WHERE RM.URI = ? AND RM.CONTENT_TYPE = ?"
			);
		ResultSet results = null;
		ResourceMetadata resourceMetadata = null;

		try {
			statement.setString(1, uri.toString());
			statement.setString(2, contentType.toString());
			results = statement.executeQuery();

			if(results.next()){
				String matchingUri = results.getString(1);
				String contentTypeString = results.getString(2);
				Timestamp lastModified = results.getTimestamp(3, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
				String entityTag = results.getString(4);
				entityTag = entityTag.replace("\"", "");

				resourceMetadata = 
					new ResourceMetadata(
						URI.create(matchingUri),
						MediaType.valueOf(contentTypeString),
						lastModified,
						new EntityTag(entityTag)
					);
			}
			unitOfWork.save();
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			unitOfWork.undo();
		}
		finally{
			try{
				if(statement != null && !statement.isClosed()){
					statement.close();
				}
				if(results != null && !results.isClosed()){
					results.close();
				}
			}
			catch(SQLException anotherSqlException){
				anotherSqlException.printStackTrace();
			}
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
	public void insertResourceMetadata(ResourceMetadata resourceMetadata) {

		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();	
		PreparedStatement statement =
			unitOfWork.createPreparedStatement(
				"INSERT INTO RESOURCE_METADATA VALUES (?, ?, ?, ?);"
			);

		try {
			statement.setString(1, resourceMetadata.getUri().toString());
			statement.setString(2, resourceMetadata.getContentType().toString());
			statement.setTimestamp(
				3, 
				new Timestamp(resourceMetadata.getLastModified().getTime()), 
				Calendar.getInstance(TimeZone.getTimeZone("UTC"))
			);
            statement.setString(4, resourceMetadata.getEntityTag().toString());
            statement.executeUpdate();
            unitOfWork.save();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            unitOfWork.undo();
        } finally {
            try{
                if(statement != null && !statement.isClosed()){
                    statement.close();
                }
            }
            catch(SQLException anotherSqlException){
                anotherSqlException.printStackTrace();
            }
        }
	}

	/**
     * Replaces the state an existing representation of metadata about a resource
     * with the provided state.
     *
     * @param resourceMetadata The desired state for the resource metadata.
     */
	@Override
	public void updateResourceMetaData(ResourceMetadata resourceMetadata) {

		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
		PreparedStatement statement =
			unitOfWork.createPreparedStatement(
				"UPDATE RESOURCE_METADATA SET URI = ?, CONTENT_TYPE = ?, LAST_MODIFIED = ?, ENTITY_TAG = ? WHERE URI = ? AND CONTENT_TYPE = ?;"
			);

		try {
			statement.setString(1, resourceMetadata.getUri().toString());
			statement.setString(2, resourceMetadata.getContentType().toString());
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
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			unitOfWork.undo();
		} finally {
			try{
				if(statement != null && !statement.isClosed()){
					statement.close();
				}
			} catch(SQLException anotherSqlException){
				anotherSqlException.printStackTrace();
			}
		}
	}

	/**
	 * Deletes the resource metadata for a resource.
	 *
	 * @param uri The URI of the resource to delete metadata for.
	 */
	@Override
	public void deleteResourceMetaData(URI uri, MediaType contentType) {
		
		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();		
		PreparedStatement statement =
			unitOfWork.createCallableStatement("DELETE FROM RESOURCE_METADATA WHERE URI = ? AND CONTENT_TYPE = ?;");
		
		try{
			statement.setString(1, uri.toString());
			statement.setString(2, contentType.toString());
			statement.executeUpdate();
			unitOfWork.save();
		}catch (SQLException sqlException) {
			sqlException.printStackTrace();
			unitOfWork.undo();
		} finally {
			try{
				if(statement != null && !statement.isClosed()){
					statement.close();
				}
			}catch(SQLException anotherSqlException) {
				anotherSqlException.printStackTrace();
			}
		}
	}

	/**
	 * Deletes the resource metadata for a resource.
	 *
	 * @param uri The URI of the resource to delete metadata for.
	 */
	@Override
	public void deleteResourceMetaData(URI uri) {
		
		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();		
		PreparedStatement statement =
			unitOfWork.createCallableStatement("DELETE FROM RESOURCE_METADATA WHERE URI = ?;");
		
		try{
			statement.setString(1, uri.toString());
			statement.executeUpdate();
			unitOfWork.save();
		}catch (SQLException sqlException) {
			sqlException.printStackTrace();
			unitOfWork.undo();
		} finally {
			try{
				if(statement != null && !statement.isClosed()){
					statement.close();
				}
			}catch(SQLException anotherSqlException) {
				anotherSqlException.printStackTrace();
			}
		}
	}
}
