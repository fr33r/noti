package infrastructure;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.jvnet.hk2.annotations.Contract;

import infrastructure.RepresentationMetadata;

/**
 * Defines the interactions available on resource representation metadata.
 *
 * @author Jon Freer
 */
@Contract
public interface RepresentationMetadataService {

	/**
	 * Retrieves representation metadata for a resource identified by the provided URI.
	 *
	 * @param uri	The URI of the resource to retrieve metadata for.
	 * @return	The resource metadata for the resource identified by
	 * the provided URI.
	 */
	RepresentationMetadata get(URI uri, Locale language, String encoding, MediaType contentType);

	/**
	 * Recards the metadata for a single resource representation.
	 *
	 * @param representationMetadata	The desired state for the representation metadata.
	 */
	void insert(RepresentationMetadata representationMetadata);

	/**
	 * Replaces the metadata for a representation with the metadata provided.
	 *
	 * @param representationMetadata	The desired state for the resource metadata.
	 */
	void put(RepresentationMetadata representationMetadata);

	/**
	 * Deletes a single representation's metadata for a resource.
	 *
	 * @param uri	The URI of the resource to delete metadata for.
	 * @param contentType	The media type used to specify which metadata to delete.
	 */
	void remove(URI uri, Locale language, String encoding, MediaType contentType);

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
	 List<RepresentationMetadata> getAll(URI uri);

	 /**
	  * Retrieves all representation metadata that matches the content location,
	  * one of the content types, one of the content languages, and one of the encodings
	  * provided. If an empty or null entry is provided for the media types, languages, or
	  * encodings, all representation will that specific criteria.
	  */
	 List<RepresentationMetadata> match(
		URI contentLocation,
		List<MediaType> mediaTypes,
		List<Locale> languages,
		List<String> encodings
	);
}
