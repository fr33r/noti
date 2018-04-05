package api.representations;

import api.representations.Representation;

import application.Audience;
import application.Notification;
import application.Target;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

public abstract class RepresentationFactory {

	private final MediaType mediaType;

	public RepresentationFactory(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public abstract Representation createNotificationRepresentation(UriInfo uriInfo, Notification notification);

	public abstract Representation createAudienceRepresentation(UriInfo uriInfo, Audience audience);

	public abstract Representation createTargetRepresentation(UriInfo uriInfo, Target target);
}
