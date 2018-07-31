package api.representations.json;

import api.representations.Representation;
import api.representations.RepresentationFactory;
import application.Audience;
import application.Notification;
import application.Target;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.net.URI;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.MediaType;

@Named("JSONRepresentationFactory")
public final class JSONRepresentationFactory extends RepresentationFactory {

  private final Tracer tracer;

  @Inject
  public JSONRepresentationFactory(Tracer tracer) {
    super(MediaType.APPLICATION_JSON_TYPE);

    this.tracer = tracer;
  }

  // perhaps create a RepresentationMetadata class to encapsulate
  // the metadata and prevent future signature changes.
  @Override
  public Representation createNotificationRepresentation(
      URI location, Locale language, Notification notification) {
    Span span =
        this.tracer
            .buildSpan("JSONRepresentationFactory#createNotificationRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Set<api.representations.json.Target> targets = new HashSet<>();
      for (Target target : notification.getTargets()) {
        targets.add(
            (api.representations.json.Target) this.createTargetRepresentation(null, null, target));
      }

      Set<api.representations.json.Audience> audiences = new HashSet<>();
      for (Audience audience : notification.getAudiences()) {
        audiences.add(
            (api.representations.json.Audience)
                this.createAudienceRepresentation(null, null, audience));
      }

      api.representations.json.NotificationStatus status =
          api.representations.json.NotificationStatus.valueOf(notification.getStatus().toString());

      // how can i set the metadata for representations, when the
      // metadata is retrieved after contruction of the representation
      // in the resource class?
      api.representations.Representation representation =
          new api.representations.json.Notification.Builder()
              .uuid(notification.getUUID())
              .content(notification.getContent())
              .status(status)
              .sendAt(notification.getSendAt())
              .sentAt(notification.getSentAt())
              .targets(targets)
              .audiences(audiences)
              .location(location)
              .language(language)
              .build();

      return representation;
    } finally {
      span.finish();
    }
  }

  @Override
  public Representation createAudienceRepresentation(
      URI location, Locale language, Audience audience) {
    Span span =
        this.tracer
            .buildSpan("JSONRepresentationFactory#createAudienceRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Set<api.representations.json.Target> targets = new HashSet<>();
      for (Target target : audience.getMembers()) {
        targets.add(
            (api.representations.json.Target) this.createTargetRepresentation(null, null, target));
      }

      api.representations.Representation representation =
          new api.representations.json.Audience.Builder()
              .uuid(audience.getUUID())
              .name(audience.getName())
              .members(targets)
              .location(location)
              .language(language)
              .build();

      return representation;
    } finally {
      span.finish();
    }
  }

  @Override
  public Representation createTargetRepresentation(URI location, Locale language, Target target) {
    Span span =
        this.tracer
            .buildSpan("JSONRepresentationFactory#createTargetRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      api.representations.Representation representation =
          new api.representations.json.Target.Builder()
              .uuid(target.getUUID())
              .name(target.getName())
              .phoneNumber(target.getPhoneNumber())
              .location(location)
              .language(language)
              .build();

      return representation;
    } finally {
      span.finish();
    }
  }
}
