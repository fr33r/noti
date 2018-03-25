package api.representations;

import api.representations.Audience;
import api.representations.Notification;
import api.representations.Representation;
import api.representations.Target;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

@Named("XMLRepresentationFactory")
public final class XMLRepresentationFactory extends RepresentationFactory {

	private final Tracer tracer;

	@Inject
	public XMLRepresentationFactory(Tracer tracer) {
		super(MediaType.APPLICATION_XML_TYPE);

		this.tracer = tracer;
	}

	@Override
	public Representation createNotificationRepresentation(UriInfo uriInfo, Notification notification) {
		Span span =
			this.tracer
				.buildSpan("XMLRepresentationFactory#createNotificationRepresentation")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
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
		} finally {
			span.finish();
		}
	}

	@Override
	public Representation createAudienceRepresentation(UriInfo uriInfo, Audience audience) {
		Span span =
			this.tracer
				.buildSpan("XMLRepresentationFactory#createAudienceRepresentation")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			Set<api.representations.xml.Target> targets = new HashSet<>();
			for(Target target : audience.getMembers()) {
				targets.add((api.representations.xml.Target)this.createTargetRepresentation(uriInfo, target));
			}

			return new api.representations.xml.Audience(
				audience.getUUID(),
				audience.getName(),
				targets
			);
		} finally {
			span.finish();
		}
	}

	@Override
	public Representation createTargetRepresentation(UriInfo uriInfo, Target target) {
		Span span =
			this.tracer
				.buildSpan("XMLRepresentationFactory#createTargetRepresentation")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			return
				new api.representations.xml.Target(
					target.getUUID(),
					target.getName(),
					target.getPhoneNumber()
				);
		} finally {
			span.finish();
		}
	}
}
