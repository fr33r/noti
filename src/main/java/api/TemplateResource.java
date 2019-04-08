package api;

import com.fasterxml.jackson.jaxrs.yaml.YAMLMediaTypes;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Defines the abstraction that exposes various HTTP operations on the template resource.
 *
 * @author jonfreer
 */
@Path("/templates")
public interface TemplateResource {

  /**
   * Handles HTTP GET requests for the template resource with the unique identifier provided.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param uuid The universally unique identifier for the template resource being retrieved.
   * @return The HTTP {@link Response}, including the representation of the requested template
   *     resource.
   */
  @GET
  @Path("{uuid}")
  @Produces({
    MediaType.APPLICATION_JSON,
    MediaType.APPLICATION_XML,
    YAMLMediaTypes.APPLICATION_JACKSON_YAML,
    YAMLMediaTypes.TEXT_JACKSON_YAML,
    "application/vnd.siren+json",
    "application/x-yaml",
    "text/x-yaml",
    "text/vnd.yaml"
  })
  Response get(
      @Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("uuid") String uuid);

  /**
   * Handles HTTP POST requests for the collection of template resources.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param template The representation of the template resource to be created.
   * @return The HTTP {@link Response}, including the representation of the requested template
   *     resource.
   */
  @POST
  @Consumes({MediaType.APPLICATION_JSON})
  @Produces({
    MediaType.APPLICATION_JSON,
    MediaType.APPLICATION_XML,
    YAMLMediaTypes.APPLICATION_JACKSON_YAML,
    YAMLMediaTypes.TEXT_JACKSON_YAML,
    "application/vnd.siren+json",
    "application/x-yaml",
    "text/x-yaml",
    "text/vnd.yaml"
  })
  Response createAndAppend(
      @Context HttpHeaders headers,
      @Context UriInfo uriInfo,
      api.representations.json.Template template);

  /**
   * Handles HTTP POST requests for the collection of template resources.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param template The representation of the template resource to be created.
   * @return The HTTP {@link Response}, including the representation of the requested template
   *     resource.
   */
  @POST
  @Consumes({MediaType.APPLICATION_XML})
  @Produces({
    MediaType.APPLICATION_JSON,
    MediaType.APPLICATION_XML,
    YAMLMediaTypes.APPLICATION_JACKSON_YAML,
    YAMLMediaTypes.TEXT_JACKSON_YAML,
    "application/vnd.siren+json",
    "application/x-yaml",
    "text/x-yaml",
    "text/vnd.yaml"
  })
  Response createAndAppend(
      @Context HttpHeaders headers,
      @Context UriInfo uriInfo,
      api.representations.xml.Template template);

  /**
   * Handles HTTP PUT requests for the template resource with the unique identifier provided.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param template The representation of the template resource to replace the current existing
   *     state.
   * @return The HTTP {@link Response}, including the representation of the requested template
   *     resource.
   */
  @PUT
  @Path("{uuid}")
  @Consumes({MediaType.APPLICATION_JSON})
  @Produces({
    MediaType.APPLICATION_JSON,
    MediaType.APPLICATION_XML,
    YAMLMediaTypes.APPLICATION_JACKSON_YAML,
    YAMLMediaTypes.TEXT_JACKSON_YAML,
    "application/vnd.siren+json",
    "application/x-yaml",
    "text/x-yaml",
    "text/vnd.yaml"
  })
  Response replace(
      @Context HttpHeaders headers,
      @Context UriInfo uriInfo,
      api.representations.json.Template template);

  /**
   * Handles HTTP PUT requests for the template resource with the unique identifier provided.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param template The representation of the template resource to replace the current existing
   *     state.
   * @return The HTTP {@link Response}, including the representation of the requested template
   *     resource.
   */
  @PUT
  @Path("{uuid}")
  @Consumes({MediaType.APPLICATION_XML})
  @Produces({
    MediaType.APPLICATION_JSON,
    MediaType.APPLICATION_XML,
    YAMLMediaTypes.APPLICATION_JACKSON_YAML,
    YAMLMediaTypes.TEXT_JACKSON_YAML,
    "application/vnd.siren+json",
    "application/x-yaml",
    "text/x-yaml",
    "text/vnd.yaml"
  })
  Response replace(
      @Context HttpHeaders headers,
      @Context UriInfo uriInfo,
      api.representations.xml.Template template);

  /**
   * Handles HTTP DELETE requests for the template resource with the unique identifier provided.
   *
   * @param uuid The universally unique identifier for the template resource being retrieved.
   * @param uriInfo Information about the URI of the HTTP request.
   * @return The HTTP {@link Response}, including the representation of the requested template
   *     resource.
   */
  @DELETE
  @Path("{uuid}")
  @Produces({
    MediaType.APPLICATION_JSON,
    MediaType.APPLICATION_XML,
    YAMLMediaTypes.APPLICATION_JACKSON_YAML,
    YAMLMediaTypes.TEXT_JACKSON_YAML,
    "application/vnd.siren+json",
    "application/x-yaml",
    "text/x-yaml",
    "text/vnd.yaml"
  })
  Response delete(@Context UriInfo uriInfo, @PathParam("uuid") String uuid);
}
