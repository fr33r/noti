package api.representations;

import api.representations.Audience;
import api.representations.Notification;
import api.representations.Representation;
import api.representations.Target;

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
