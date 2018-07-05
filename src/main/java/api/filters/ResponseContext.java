package api.filters;

import java.util.Date;
import java.util.List;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.EntityTag;

public interface ResponseContext extends ContainerResponseContext {

  /**
   * Get the encoding(s) of the entity.
   *
   * @return The encoding(s) of the entity or null if not specified.
   */
  List<String> getEncodings();

  /**
   * Checks if the HTTP status code of the response is equivalent to the status code provided.
   *
   * @param code The HTTP status code to compare against.
   * @return {@code true} if the HTTP status codes match; {@code false} otherwise.
   */
  boolean statusIs(int code);

  /**
   * Checks if the HTTP status code of the response belongs the 'informational' status code class.
   * All HTTP status codes that start with a '1' belong to the 'informational' class.
   *
   * @return {@code true} if the HTTP status code begins with a '1'; {@code false} otherwise.
   * @see <a href='https://tools.ietf.org/html/rfc7231#section-6.2'>RFC7231 Section 6.2</a>
   */
  boolean statusIsInformational();

  /**
   * Checks if the HTTP status code of the response belongs the 'successful' status code class. All
   * HTTP status codes that start with a '2' belong to the 'successful' class.
   *
   * @return {@code true} if the HTTP status code begins with a '2'; {@code false} otherwise.
   * @see <a href='https://tools.ietf.org/html/rfc7231#section-6.3'>RFC7231 Section 6.3</a>
   */
  boolean statusIsSuccessful();

  /**
   * Checks if the HTTP status code of the response belongs the 'redirection' status code class. All
   * HTTP status codes that start with a '3' belong to the 'redirection' class.
   *
   * @return {@code true} if the HTTP status code begins with a '3'; {@code false} otherwise.
   * @see <a href='https://tools.ietf.org/html/rfc7231#section-6.4'>RFC7231 Section 6.4</a>
   */
  boolean statusIsRedirection();

  /**
   * Checks if the HTTP status code of the response belongs the 'client error' status code class.
   * All HTTP status codes that start with a '4' belong to the 'client error' class.
   *
   * @return {@code true} if the HTTP status code begins with a '4'; {@code false} otherwise.
   * @see <a href='https://tools.ietf.org/html/rfc7231#section-6.5'>RFC7231 Section 6.5</a>
   */
  boolean statusIsClientError();

  /**
   * Checks if the HTTP status code of the response belongs the 'server error' status code class.
   * All HTTP status codes that start with a '5' belong to the 'server error' class.
   *
   * @return {@code true} if the HTTP status code begins with a '5'; {@code false} otherwise.
   * @see <a href='https://tools.ietf.org/html/rfc7231#section-6.6'>RFC7231 Section 6.6</a>
   */
  boolean statusIsServerError();

  /**
   * Modifies the entity tag.
   *
   * @param entityTag The new entity tag.
   */
  void setEntityTag(EntityTag entityTag);

  /**
   * Modifies the last modified date.
   *
   * @param lastModified The new last modified date.
   */
  void setLastModified(Date lastModified);
}
