package api.resources;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import infrastructure.ResourceMetadata;

import api.representations.Notification;
import javax.inject.Inject;
import application.NotificationService;
import infrastructure.ResourceMetadataService;
import infrastructure.EntityTagService;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Represents a notification resource for this RESTful API.
 * @author jonfreer
 */
public class NotificationResource implements api.NotificationResource{

	private final NotificationService notificationService;
	private final ResourceMetadataService resourceMetadataService;
	private final EntityTagService entityTagService;

	/**
	 * Construct a new {@link NotificationResource} instance.
	 * @param notificationService Application service that orchestrates various operations with notifications.
	 */
	@Inject
	public NotificationResource(
		NotificationService notificationService,
		ResourceMetadataService resourceMetadataService,
		EntityTagService entityTagService
	) {
		this.notificationService = notificationService;
		this.resourceMetadataService = resourceMetadataService;
		this.entityTagService = entityTagService;
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

		//search for matches; whole purpose is to see if metadata needs to be persisted. can this go in a filter?
		ResourceMetadata resourceMetadata = null;
		for(MediaType mediaType : headers.getAcceptableMediaTypes()) {
			resourceMetadata =
				this.resourceMetadataService.getResourceMetadata(uriInfo.getRequestUri(), mediaType);
			if(resourceMetadata != null) break;
		}

		if(resourceMetadata == null) {
			EntityTag entityTag = this.entityTagService.generateTag(notification);
			Date lastModified = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
			for(MediaType mediaType : headers.getAcceptableMediaTypes()) {
				//store resource metadata.
				this.resourceMetadataService.insertResourceMetadata(
					new ResourceMetadata(uriInfo.getRequestUri(), mediaType, lastModified, entityTag)
				);
			}	
		}

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

		//store resource metadata.
		Date lastModified = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		Notification notiCreated = this.notificationService.getNotification(uuid);
		EntityTag entityTag = this.entityTagService.generateTag(notiCreated);

		this.resourceMetadataService.insertResourceMetadata(
			new ResourceMetadata(location, headers.getMediaType(), lastModified, entityTag)
		);

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
		ResourceMetadata resourceMetadata = 
			this.resourceMetadataService.getResourceMetadata(
				uriInfo.getRequestUri(),
				headers.getMediaType()
			);

		ResponseBuilder responseBuilder = Response.noContent();

		if(resourceMetadata != null){

			//update resource metadata.
			Date lastModified = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
			EntityTag entityTag = this.entityTagService.generateTag(notification);

			this.resourceMetadataService.updateResourceMetaData(
				new ResourceMetadata(
					uriInfo.getRequestUri(),
					headers.getMediaType(),
					lastModified,
					entityTag
				)
			);

			resourceMetadata =
				this.resourceMetadataService.getResourceMetadata(
					uriInfo.getRequestUri(),
					headers.getMediaType()
				);

			responseBuilder
				.header("Last-Modified", resourceMetadata.getLastModified())
				.tag(resourceMetadata.getEntityTag());
		}
		return responseBuilder.build();
	}

	/**
	 * Handles HTTP DELETE requests for the notification with the unique identifier provided.
	 * @param uuid The unique identifier for the notification resource being deleted.
	 * @return An instance of {@link Response} representing the HTTP response.
	 */
	@Override
	public Response delete(UriInfo uriInfo, String uuid) {
		this.notificationService.deleteNotification(UUID.fromString(uuid));
		this.resourceMetadataService.deleteResourceMetaData(uriInfo.getRequestUri());
		return Response.noContent().build();
	}
}
