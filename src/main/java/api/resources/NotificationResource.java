package api.resources;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import api.representations.Notification;
import javax.inject.Inject;
import application.NotificationService;

import java.net.URI;
import java.util.UUID;

/**
 * Represents a notification resource for this RESTful API.
 * @author jonfreer
 */
public class NotificationResource implements api.NotificationResource{

	private final NotificationService notificationService;

	/**
	 * Construct a new {@link NotificationResource} instance.
	 * @param notificationService Application service that orchestrates various operations with notifications.
	 */
	@Inject
	public NotificationResource(
		NotificationService notificationService
	) {
		this.notificationService = notificationService;
	}

	/**
	 * Handles HTTP GET requests for the notification with the unique identifier provided.
	 * @param uuid The unique identifier for the notification resource being retrieved.
	 * @return An instance of {@link Response} representing the HTTP response, including 
	 * the representation of requested notification resource.
	 */
	@Override
	public Response get(HttpHeaders headers, UriInfo uriInfo, String uuid) {
		Notification notification = 
			this.notificationService.getNotification(UUID.fromString(uuid));
		return Response.ok(notification).build();
	}

	/**
	 * Handles HTTP POST requests for the collection of notification resources.
	 * @param uriInfo Information about the request URI, so that it can be leveraged when constructing the response.
	 * @param notification The representation of the notification resource to be created.
	 * @return An instance of {@link Response} representing the HTTP response.
	 */
	@Override
	public Response createAndAppend(HttpHeaders headers, UriInfo uriInfo, Notification notification) {
		UUID uuid = this.notificationService.createNotification(notification);
		URI location =
			UriBuilder
				.fromUri(uriInfo.getRequestUri())
				.path("/{uuid}/")
				.build(uuid.toString());
		return Response.created(location).build();
	}

	/**
	 * Handles HTTP PUT requests for the notification with the unique identifier provided.
	 * @param uriInfo Information about the request URI, so that it can be leveraged when constructing the response.
	 * @param notification The representation of the notification resource to replace the current existing state.
	 * @return An instance of {@link Response} representing the HTTP response.
	 */
	@Override
	public Response replace(HttpHeaders headers, UriInfo uriInfo, Notification notification) {
		this.notificationService.updateNotification(notification);
		return Response.noContent().build();
	}

	/**
	 * Handles HTTP DELETE requests for the notification with the unique identifier provided.
	 * @param uuid The unique identifier for the notification resource being deleted.
	 * @return An instance of {@link Response} representing the HTTP response.
	 */
	@Override
	public Response delete(UriInfo uriInfo, String uuid) {
		this.notificationService.deleteNotification(UUID.fromString(uuid));
		return Response.noContent().build();
	}
}
