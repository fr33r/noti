package api.resources;

import api.representations.RepresentationFactory;
import api.representations.Notification;

import application.NotificationService;

import java.net.URI;
import java.util.UUID;

import javax.inject.Named;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

/**
 * Represents a notification resource for this RESTful API.
 * @author jonfreer
 */
public class NotificationResource implements api.NotificationResource{

	private final NotificationService notificationService;
	private final RepresentationFactory jsonRepresentationFactory;
	private final RepresentationFactory xmlRepresentationFactory;
	private final RepresentationFactory sirenRepresentationFactory;
	private final Tracer tracer;

	/**
	 * Construct a new {@link NotificationResource} instance.
	 * @param notificationService Application service that orchestrates various operations with notifications.
	 */
	@Inject
	public NotificationResource(
		NotificationService notificationService,
		@Named("JSONRepresentationFactory") RepresentationFactory jsonRepresentationFactory,
		@Named("XMLRepresentationFactory") RepresentationFactory xmlRepresentationFactory,
		@Named("SirenRepresentationFactory") RepresentationFactory sirenRepresentationFactory,
		Tracer tracer
	) {
		this.notificationService = notificationService;
		this.jsonRepresentationFactory = jsonRepresentationFactory;
		this.xmlRepresentationFactory = xmlRepresentationFactory;
		this.sirenRepresentationFactory = sirenRepresentationFactory;
		this.tracer = tracer;
	}

	/**
	 * Handles HTTP GET requests for the notification with the unique identifier provided.
	 * @param uuid The unique identifier for the notification resource being retrieved.
	 * @return An instance of {@link Response} representing the HTTP response, including 
	 * the representation of requested notification resource.
	 */
	@Override
	public Response get(HttpHeaders headers, UriInfo uriInfo, String uuid) {
		Span span = this.tracer.buildSpan("NotificationResource#get").start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			Notification notification = 
				this.notificationService.getNotification(UUID.fromString(uuid));

			api.representations.Representation representation;
			if(headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_XML_TYPE)) {
				representation =
					this.xmlRepresentationFactory.createNotificationRepresentation(uriInfo, notification);
			} else if(headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_JSON_TYPE)) {
				representation =
					this.jsonRepresentationFactory.createNotificationRepresentation(uriInfo, notification);
			} else {
				representation =
					this.sirenRepresentationFactory.createNotificationRepresentation(uriInfo, notification);
			}
			return Response.ok(representation).build();
		} finally {
			span.finish();
		}
	}

	/**
	 * Handles HTTP POST requests for the collection of notification resources.
	 * @param uriInfo Information about the request URI, so that it can be leveraged when constructing the response.
	 * @param notification The representation of the notification resource to be created.
	 * @return An instance of {@link Response} representing the HTTP response.
	 */
	@Override
	public Response createAndAppend(HttpHeaders headers, UriInfo uriInfo, Notification notification) {
		Span span = this.tracer.buildSpan("NotificationResource#createAndAppend").start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			UUID uuid = this.notificationService.createNotification(notification);
			URI location =
				UriBuilder
					.fromUri(uriInfo.getRequestUri())
					.path("/{uuid}/")
					.build(uuid.toString());
			return Response.created(location).build();
		} finally {
			span.finish();
		}
	}

	/**
	 * Handles HTTP PUT requests for the notification with the unique identifier provided.
	 * @param uriInfo Information about the request URI, so that it can be leveraged when constructing the response.
	 * @param notification The representation of the notification resource to replace the current existing state.
	 * @return An instance of {@link Response} representing the HTTP response.
	 */
	@Override
	public Response replace(HttpHeaders headers, UriInfo uriInfo, Notification notification) {
		Span span = this.tracer.buildSpan("NotificationResource#replace").start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			this.notificationService.updateNotification(notification);
			return Response.noContent().build();
		} finally {
			span.finish();
		}
	}

	/**
	 * Handles HTTP DELETE requests for the notification with the unique identifier provided.
	 * @param uuid The unique identifier for the notification resource being deleted.
	 * @return An instance of {@link Response} representing the HTTP response.
	 */
	@Override
	public Response delete(UriInfo uriInfo, String uuid) {
		Span span = this.tracer.buildSpan("NotificationResource#delete").start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			this.notificationService.deleteNotification(UUID.fromString(uuid));
			return Response.noContent().build();
		} finally {
			span.finish();
		}
	}
}
