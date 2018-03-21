package api.representations;

import api.representations.Audience;
import api.representations.Notification;
import api.representations.Representation;
import api.representations.Target;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.inject.Named;

@Named("JSONRepresentationFactory")
public final class JSONRepresentationFactory extends RepresentationFactory {

	public JSONRepresentationFactory() {
		super(MediaType.APPLICATION_JSON_TYPE);
	}

	@Override
	public Representation createNotificationRepresentation(UriInfo uriInfo, Notification notification) {
		Set<api.representations.json.Target> targets = new HashSet<>();
		for(Target target : notification.getTargets()) {
			targets.add((api.representations.json.Target)this.createTargetRepresentation(uriInfo, target));
		}

		Set<api.representations.json.Audience> audiences = new HashSet<>();
		for(Audience audience : notification.getAudiences()) {
			audiences.add((api.representations.json.Audience)this.createAudienceRepresentation(uriInfo, audience));
		}

		api.representations.json.NotificationStatus status =
			api.representations.json.NotificationStatus.valueOf(notification.getStatus().toString());

		return new api.representations.json.Notification(
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
		Set<api.representations.json.Target> targets = new HashSet<>();
		for(Target target : audience.getMembers()) {
			targets.add((api.representations.json.Target)this.createTargetRepresentation(uriInfo, target));
		}
		
		return new api.representations.json.Audience(
			audience.getUUID(),
			audience.getName(),
			targets
		);
	}

	@Override
	public Representation createTargetRepresentation(UriInfo uriInfo, Target target) {
		return
			new api.representations.json.Target(
				target.getUUID(),
				target.getName(),
				target.getPhoneNumber()
			);
	}
}
