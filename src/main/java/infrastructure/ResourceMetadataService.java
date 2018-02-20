package infrastructure;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.jvnet.hk2.annotations.Contract;

import infrastructure.ResourceMetadata;

/**
 * Defines the interactions available on resource representation metadata.
 *
 * @author Jon Freer
 */
@Contract
public interface ResourceMetadataService {

	/**
	 * Retrieves representation metadata for a resource identified by the provided URI.
	 *
	 * @param uri	The URI of the resource to retrieve metadata for.
	 * @return	The resource metadata for the resource identified by
	 * the provided URI.
	 */
	ResourceMetadata get(URI uri, MediaType contentType);

	/**
	 * Recards the metadata for a single resource representation.
	 *
	 * @param resourceMetadata	The desired state for the representation metadata.
	 */
	void insert(ResourceMetadata resourceMetadata);

	/**
	 * Replaces the metadata for a representation with the metadata provided.
	 *
	 * @param resourceMetadata	The desired state for the resource metadata.
	 */
	void put(ResourceMetadata resourceMetadata);

	/**
	 * Deletes a single representation's metadata for a resource.
	 *
	 * @param uri	The URI of the resource to delete metadata for.
	 * @param contentType	The media type used to specify which metadata to delete.
	 */
	void remove(URI uri, MediaType contentType);

	/**
	 * Deletes all representation metadata for a resource identified by the provided URI.
	 *
	 * @param uri	The URI of the resource to delete all metadata for.
	 */
	void removeAll(URI uri);

	/**
	 * Retrieves all resource representation metadata for a resource identified by
	 * the provided URI.
	 *
	 * @param uri	The URI of the resource to retrieve representation metadata for.
	 * @return	The representation metadata for the resource identified by the provided URI.
	 */
	 List<ResourceMetadata> getAll(URI uri);
}
