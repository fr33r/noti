package api.representations;

import api.representations.Audience;
import api.representations.Notification;
import api.representations.Representation;
import api.representations.Target;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Named("XMLRepresentationFactory")
public final class XMLRepresentationFactory extends RepresentationFactory {

	public XMLRepresentationFactory() {
		super(MediaType.APPLICATION_XML_TYPE);
	}

	@Override
	public Representation createNotificationRepresentation(UriInfo uriInfo, Notification notification) {
		Set<api.representations.xml.Target> targets = new HashSet<>();
		for(Target target : notification.getTargets()) {
			targets.add((api.representations.xml.Target)this.createTargetRepresentation(uriInfo, target));
		}

		Set<api.representations.xml.Audience> audiences = new HashSet<>();
		for(Audience audience : notification.getAudiences()) {
			audiences.add((api.representations.xml.Audience)this.createAudienceRepresentation(uriInfo, audience));
		}

		api.representations.xml.NotificationStatus status =
			api.representations.xml.NotificationStatus.valueOf(notification.getStatus().toString());

		return new api.representations.xml.Notification(
			notification.getUUID(),
			notification.getContent(),
			status,
			targets,
			audiences,
			notification.getSendAt(),
			notification.getSentAt()
		);
	}

	@Override
	public Representation createAudienceRepresentation(UriInfo uriInfo, Audience audience) {
		Set<api.representations.xml.Target> targets = new HashSet<>();
		for(Target target : audience.getMembers()) {
			targets.add((api.representations.xml.Target)this.createTargetRepresentation(uriInfo, target));
		}
		
		return new api.representations.xml.Audience(
			audience.getUUID(),
			audience.getName(),
			targets
		);
	}

	@Override
	public Representation createTargetRepresentation(UriInfo uriInfo, Target target) {
		return
			new api.representations.xml.Target(
				target.getUUID(),
				target.getName(),
				target.getPhoneNumber()
			);
	}
}
