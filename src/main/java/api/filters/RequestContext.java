package api.filters;

import java.net.URI;
import java.util.List;
import javax.ws.rs.container.ContainerRequestContext;

public interface RequestContext extends ContainerRequestContext {

  /**
   * Checks if the HTTP method of the corresponding request is equivalent to the HTTP method
   * provided.
   *
   * @param method The HTTP method to compare against.
   * @return {@code true} if the HTTP methods match; {@code false} otherwise.
   */
  boolean methodIs(String method);

  /**
   * Get the URI of the request.
   *
   * @return URI of the request.
   */
  URI getRequestUri();

  /**
   * Get a list of encodings that are acceptable for the response.
   *
   * @return A readonly list of acceptable encodings sorted according to their q-value, with highest
   *     preference first.
   */
  List<String> getAcceptableEncodings();

  /**
   * Get a list of character sets that are acceptable for the response.
   *
   * @return A readonly list of acceptable character sets sorted accoring to their q-value, with
   *     highest preference first.
   */
  List<String> getAcceptableCharsets();

  /**
   * Get the encoding(s) of the entity.
   *
   * @return The encoding(s) of the entity or null if not specified.
   */
  List<String> getEncodings();
}
