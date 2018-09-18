package api;

import api.representations.json.Notification;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Defines the abstraction that exposes various HTTP operations on the notification resource.
 *
 * @author jonfreer
 */
@Path("/notifications")
public interface NotificationResource {

  /**
   * Handles HTTP GET requests for the notification collection.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param messageExternalID The external identifier of a message associated with a notification.
   * @param skip The number of notifications to skip when in the collection.
   * @param take The maximum number of notifications to return in the response.
   * @return The HTTP {@link Response}, including the representations of the requested notification
   *     collection.
   */
  @GET
  Response getCollection(
      @Context HttpHeaders headers,
      @Context UriInfo uriInfo,
      @QueryParam("messageExternalID") String messageExternalID,
      @QueryParam("skip") Integer skip,
      @QueryParam("take") Integer take);

  /**
   * Handles HTTP GET requests for the notification with the unique identifier provided.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param uuid The universally unique identifier for the notification resource being retrieved.
   * @return The HTTP {@link Response}, including the representation of the requested notification
   *     resource.
   */
  @GET
  @Path("{uuid}")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/vnd.siren+json"})
  Response get(
      @Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("uuid") String uuid);

  /**
   * Handles HTTP POST requests for the collection of notification resources.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param notification The representation of the notification resource to be created.
   * @return The HTTP {@link Response}, including the representation of the requested notification
   *     resource.
   */
  @POST
  @Consumes({MediaType.APPLICATION_JSON})
  Response createAndAppend(
      @Context HttpHeaders headers, @Context UriInfo uriInfo, Notification notification);

  /**
   * Handles HTTP PUT requests for the notification resource with the unique identifier provided.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param notification The representation of the notification resource to replace the current
   *     existing state.
   * @return The HTTP {@link Response}, including the representation of the requested notification
   *     resource.
   */
  @PUT
  @Path("{uuid}")
  @Consumes({MediaType.APPLICATION_JSON})
  Response replace(
      @Context HttpHeaders headers, @Context UriInfo uriInfo, Notification notification);

  /**
   * Handles HTTP DELETE requests for the notification resource with the unique identifier provided.
   *
   * @param uuid The universally unique identifier for the notification resource being retrieved.
   * @param uriInfo Information about the URI of the HTTP request.
   * @return The HTTP {@link Response}, including the representation of the requested notification
   *     resource.
   */
  @DELETE
  @Path("{uuid}")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/vnd.siren+json"})
  Response delete(@Context UriInfo uriInfo, @PathParam("uuid") String uuid);

  /**
   * Handles HTTP GET requests for the target collection associated with the notification resource
   * with the unique identifier provided.
   *
   * @param headers The headers from the HTTP request.
   * @param uuid The universally unique identifier for the notification resource being retrieved.
   * @param uriInfo Information about the URI of the HTTP request.
   * @return The HTTP {@link Response}, including the representation of the requested notification
   *     resource.
   */
  @GET
  @Path("{uuid}/targets")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/vnd.siren+json"})
  Response getTargetCollection(
      @Context HttpHeaders headers,
      @Context UriInfo uriInfo,
      @PathParam("uuid") String uuid,
      @QueryParam("skip") Integer skip,
      @QueryParam("take") Integer take);

  /**
   * Handles HTTP GET requests for the audience collection associated with the notification resource
   * with the unique identifier provided.
   *
   * @param headers The headers from the HTTP request.
   * @param uuid The universally unique identifier for the notification resource being retrieved.
   * @param uriInfo Information about the URI of the HTTP request.
   * @return The HTTP {@link Response}, including the representation of the requested notification
   *     resource.
   */
  @GET
  @Path("{uuid}/audiences")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/vnd.siren+json"})
  Response getAudienceCollection(
      @Context HttpHeaders headers,
      @Context UriInfo uriInfo,
      @PathParam("uuid") String uuid,
      @QueryParam("skip") Integer skip,
      @QueryParam("take") Integer take);
}
