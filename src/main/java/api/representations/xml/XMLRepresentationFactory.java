package api.representations.xml;

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

/**
 * Defines the factory responsible for constructing {code application/xml} representations.
 *
 * @author Jon Freer
 */
@Named("XMLRepresentationFactory")
public final class XMLRepresentationFactory extends RepresentationFactory {

  private final Tracer tracer;

  /**
   * Constructs a new {@link XMLRepresentationFactory}.
   *
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   */
  @Inject
  public XMLRepresentationFactory(Tracer tracer) {
    super(MediaType.APPLICATION_XML_TYPE);

    this.tracer = tracer;
  }

  /**
   * Constructs a {@link Notification} representation.
   *
   * @param location The content location of the {@link Notification} representation.
   * @param language The content language of the {@link Notification} representation.
   * @param notification The notification state expressed by the {@link Notification} representation
   *     being constructed.
   * @return The {@link Notification} representation.
   */
  @Override
  public Representation createNotificationRepresentation(
      URI location, Locale language, Notification notification) {
    Span span =
        this.tracer
            .buildSpan("XMLRepresentationFactory#createNotificationRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Set<api.representations.xml.Target> targets = new HashSet<>();
      for (Target target : notification.getTargets()) {
        targets.add(
            (api.representations.xml.Target) this.createTargetRepresentation(null, null, target));
      }

      Set<api.representations.xml.Audience> audiences = new HashSet<>();
      for (Audience audience : notification.getAudiences()) {
        audiences.add(
            (api.representations.xml.Audience)
                this.createAudienceRepresentation(null, null, audience));
      }

      api.representations.xml.NotificationStatus status =
          api.representations.xml.NotificationStatus.valueOf(notification.getStatus().toString());

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

  /**
   * Constructs a {@link Audience} representation.
   *
   * @param location The content location of the {@link Audience} representation.
   * @param language The content language of the {@link Audience} representation.
   * @param audience The audience state expressed by the {@link Audience} representation being
   *     constructed.
   * @return The {@link Audience} representation.
   */
  @Override
  public Representation createAudienceRepresentation(
      URI location, Locale language, Audience audience) {
    Span span =
        this.tracer
            .buildSpan("XMLRepresentationFactory#createAudienceRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Set<api.representations.xml.Target> targets = new HashSet<>();
      for (Target target : audience.getMembers()) {
        targets.add(
            (api.representations.xml.Target) this.createTargetRepresentation(null, null, target));
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

  /**
   * Constructs a {@link Target} representation.
   *
   * @param location The content location of the {@link Target} representation.
   * @param language The content language of the {@link Target} representation.
   * @param target The target state expressed by the {@link Target} representation being
   *     constructed.
   * @return The {@link Target} representation.
   */
  @Override
  public Representation createTargetRepresentation(URI location, Locale language, Target target) {
    Span span =
        this.tracer
            .buildSpan("XMLRepresentationFactory#createTargetRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
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

  @Override
  public Representation createNotificationCollectionRepresentation(
      URI location,
      Locale language,
      Set<Notification> notifications,
      Integer skip,
      Integer take,
      Integer total) {
    Span span =
        this.tracer
            .buildSpan("XMLRepresentationFactory#createNotificationCollectionRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      api.representations.xml.NotificationCollection.Builder builder =
          new api.representations.xml.NotificationCollection.Builder();
      for (Notification notification : notifications) {
        Representation notificationRepresentation =
            this.createNotificationRepresentation(location, language, notification);
        builder.addNotification(notificationRepresentation);
      }
      return builder.total(total).build();
    } finally {
      span.finish();
    }
  }
}
