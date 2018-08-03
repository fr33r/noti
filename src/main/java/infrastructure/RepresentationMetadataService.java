package infrastructure;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import org.jvnet.hk2.annotations.Contract;

/**
 * Defines the interactions available on resource representation metadata.
 *
 * @author Jon Freer
 */
@Contract
public interface RepresentationMetadataService {

  /**
   * Retrieves {@link infrastructure.RepresentationMetadata}.
   *
   * @param uri The content location of the representation.
   * @param language The content language of the representation.
   * @param encoding The content encoding of the representation.
   * @param contentType the content type of the representation.
   * @return The representation metdata with the content location, language, encoding and type
   *     provided.
   */
  RepresentationMetadata get(URI uri, Locale language, String encoding, MediaType contentType);

  /**
   * Records the metadata for a single resource representation.
   *
   * @param representationMetadata The desired state for the representation metadata.
   */
  void insert(RepresentationMetadata representationMetadata);

  /**
   * Replaces the metadata for a representation with the metadata provided.
   *
   * @param representationMetadata The desired state for the representation metadata.
   */
  void put(RepresentationMetadata representationMetadata);

  /**
   * Deletes the representation metadata for a resource representation.
   *
   * @param uri The content location of the representation.
   * @param language The content language of the representation.
   * @param encoding The content encoding of the representation.
   * @param contentType the content type of the representation.
   */
  void remove(URI uri, Locale language, String encoding, MediaType contentType);

  /**
   * Deletes all representation metadata with the provide content location.
   *
   * @param uri The content location of the representation.
   */
  void removeAll(URI uri);

  /**
   * Retrieves all resource representation metadata with the content location provided.
   *
   * @param uri The content location of the representation.
   * @return The representation metdata with the content location provided.
   */
  List<RepresentationMetadata> getAll(URI uri);
}
