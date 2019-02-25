package api;

import com.fasterxml.jackson.jaxrs.yaml.YAMLMediaTypes;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Defines the abstraction that exposes various HTTP operations on the message resource.
 *
 * @author Jon Freer
 */
@Path("/notifications/{uuid}/messages")
public interface MessageResource {

  /**
   * Handles HTTP GET requests for the message collection associated with the notification resource
   * with the unique identifier provided.
   *
   * @param headers The headers from the HTTP request.
   * @param uuid The universally unique identifier for the notification resource being retrieved.
   * @param uriInfo Information about the URI of the HTTP request.
   * @return The HTTP {@link Response}, including the representation of the requested notification
   *     resource.
   */
  @GET
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
  Response getCollection(
      @Context HttpHeaders headers,
      @Context UriInfo uriInfo,
      @PathParam("uuid") String uuid,
      @QueryParam("skip") Integer skip,
      @QueryParam("take") Integer take);

  /**
   * Handles HTTP GET requests for the notification message with the ID provided.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param notificationUUID The universally unique identifier for the notification the messages is
   *     associated with.
   * @param id The identifier of the message.
   * @return The HTTP {@link Response}, including the representation of the requested message
   *     resource.
   */
  @GET
  @Path("{id}")
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
      @Context HttpHeaders headers,
      @Context UriInfo uriInfo,
      @PathParam("uuid") String notificationUUID,
      @PathParam("id") Integer id);

  /**
   * Handles HTTP PUT requests for the notification message with the ID provided.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param notificationUUID The universally unique identifier for the notification the messages is
   *     associated with.
   * @param message The representation of the message resource to replace existing state.
   * @return The HTTP {@link Response}, including the representation of the requested message
   *     resource.
   */
  @PUT
  @Path("{id}")
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
      @PathParam("uuid") String notificationUUID,
      api.representations.json.Message message);

  /**
   * Handles HTTP PUT requests for the notification message with the ID provided.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param notificationUUID The universally unique identifier for the notification the messages is
   *     associated with.
   * @param message The representation of the message resource to replace existing state.
   * @return The HTTP {@link Response}, including the representation of the requested message
   *     resource.
   */
  @PUT
  @Path("{id}")
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
      @PathParam("uuid") String notificationUUID,
      api.representations.xml.Message message);

  @POST
  @Path("/{id}/provider/twilio/logs")
  @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
  Response createAndAppend(
      @Context HttpHeaders headers,
      @Context UriInfo uriInfo,
      @PathParam("uuid") String notificationUUID,
      @PathParam("id") Integer id,
      MultivaluedMap<String, String> messageLog);
}
