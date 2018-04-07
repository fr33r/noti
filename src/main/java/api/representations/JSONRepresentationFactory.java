package api.representations;

import api.representations.Representation;

import application.Audience;
import application.Notification;
import application.Target;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.inject.Inject;
import javax.inject.Named;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

@Named("JSONRepresentationFactory")
public final class JSONRepresentationFactory extends RepresentationFactory {

	private final Tracer tracer;

	@Inject
	public JSONRepresentationFactory(Tracer tracer) {
		super(MediaType.APPLICATION_JSON_TYPE);

		this.tracer = tracer;
	}

	@Override
	public Representation createNotificationRepresentation(UriInfo uriInfo, Notification notification) {
		Span span =
			this.tracer
				.buildSpan("JSONRepresentationFactory#createNotificationRepresentation")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
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
		} finally {
			span.finish();
		}
	}

	@Override
	public Representation createAudienceRepresentation(UriInfo uriInfo, Audience audience) {
		Span span =
			this.tracer
				.buildSpan("JSONRepresentationFactory#createAudienceRepresentation")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			Set<api.representations.json.Target> targets = new HashSet<>();
			for(Target target : audience.getMembers()) {
				targets.add((api.representations.json.Target)this.createTargetRepresentation(uriInfo, target));
			}
		
			return new api.representations.json.Audience(
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
				.buildSpan("JSONRepresentationFactory#createTargetRepresentation")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			return
				new api.representations.json.Target(
					target.getUUID(),
					target.getName(),
					target.getPhoneNumber()
				);
		} finally {
			span.finish();
		}
	}
}
