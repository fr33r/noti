package api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import api.representations.Notification;
import javax.ws.rs.PathParam;

/**
 * Defines the contract that exposes various HTTP operations on the notification resource.
 * @author jonfreer
 */
@Path("/notifications")
public interface NotificationResource {

	/**
	 * Handles HTTP GET requests for the notification with the unique identifier provided.
	 * @param uuid The unique identifier for the notification resource being retrieved.
	 * @return An instance of {@link Response} representing the HTTP response, including 
	 * the representation of requested notification resource.
	 */
	@GET
	@Path("{uuid}")
	@Produces({MediaType.APPLICATION_JSON})
	Response get(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("uuid") String uuid);

	/**
	 * Handles HTTP POST requests for the collection of notification resources.
	 * @param uriInfo Information about the request URI, so that it can be leveraged when constructing the response.
	 * @param notification The representation of the notification resource to be created.
	 * @return An instance of {@link Response} representing the HTTP response.
	 */
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	Response createAndAppend(@Context HttpHeaders headers, @Context UriInfo uriInfo, Notification notification);

	/**
	 * Handles HTTP PUT requests for the notification with the unique identifier provided.
	 * @param uriInfo Information about the request URI, so that it can be leveraged when constructing the response.
	 * @param notification The representation of the notification resource to replace the current existing state.
	 * @return An instance of {@link Response} representing the HTTP response.
	 */
	@PUT
	@Path("{uuid}")
	@Consumes({MediaType.APPLICATION_JSON})
	Response replace(@Context HttpHeaders headers, @Context UriInfo uriInfo, Notification notification);

	/**
	 * Handles HTTP DELETE requests for the notification with the unique identifier provided.
	 * @param uuid The unique identifier for the notification resource being deleted.
	 * @return An instance of {@link Response} representing the HTTP response.
	 */
	@DELETE
	@Path("{uuid}")
	@Produces({MediaType.APPLICATION_JSON})
	Response delete(@Context UriInfo uriInfo, @PathParam("uuid") String uuid);
}

