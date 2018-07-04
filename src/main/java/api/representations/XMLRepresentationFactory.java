package api.representations;

import api.representations.Representation;

import application.Audience;
import application.Notification;
import application.Target;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.MediaType;

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
	public Representation createNotificationRepresentation(
		URI location,
		Locale language,
		Notification notification
	) {
		Span span =
			this.tracer
				.buildSpan("XMLRepresentationFactory#createNotificationRepresentation")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			Set<api.representations.xml.Target> targets = new HashSet<>();
			for(Target target : notification.getTargets()) {
				targets.add(
					(api.representations.xml.Target)
					this.createTargetRepresentation(null, null, target)
				);
			}

			Set<api.representations.xml.Audience> audiences = new HashSet<>();
			for(Audience audience : notification.getAudiences()) {
				audiences.add(
					(api.representations.xml.Audience)
					this.createAudienceRepresentation(null, null, audience)
				);
			}

			api.representations.xml.NotificationStatus status =
				api.representations.xml.NotificationStatus.valueOf(
					notification.getStatus().toString()
				);

			return new api.representations.xml.Notification.Builder()
				.uuid(notification.getUUID())
				.content(notification.getContent())
				.status(status)
				.targets(targets)
				.audiences(audiences)
				.sendAt(notification.getSendAt())
				.sentAt(notification.getSentAt())
				.language(language)
				.location(location)
				.build();

		} finally {
			span.finish();
		}
	}

	@Override
	public Representation createAudienceRepresentation(
		URI location,
		Locale language,
		Audience audience
	) {
		Span span =
			this.tracer
				.buildSpan("XMLRepresentationFactory#createAudienceRepresentation")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			Set<api.representations.xml.Target> targets = new HashSet<>();
			for(Target target : audience.getMembers()) {
				targets.add(
					(api.representations.xml.Target)
					this.createTargetRepresentation(null, null, target)
				);
			}

			return new api.representations.xml.Audience.Builder()
				.uuid(audience.getUUID())
				.name(audience.getName())
				.members(targets)
				.language(language)
				.location(location)
				.build();
		} finally {
			span.finish();
		}
	}

	@Override
	public Representation createTargetRepresentation(
		URI location,
		Locale language,
		Target target
	) {
		Span span =
			this.tracer
				.buildSpan("XMLRepresentationFactory#createTargetRepresentation")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			return new api.representations.xml.Target.Builder()
				.uuid(target.getUUID())
				.name(target.getName())
				.phoneNumber(target.getPhoneNumber())
				.language(language)
				.location(location)
				.build();
		} finally {
			span.finish();
		}
	}
}
