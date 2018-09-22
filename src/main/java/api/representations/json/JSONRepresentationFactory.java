package api.representations.json;

import api.representations.Representation;
import api.representations.RepresentationFactory;
import application.ApplicationException;
import application.Audience;
import application.Message;
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
 * Defines the factory responsible for constructing {code application/json} representations.
 *
 * @author Jon Freer
 */
@Named("JSONRepresentationFactory")
public final class JSONRepresentationFactory extends RepresentationFactory {

  private final Tracer tracer;

  /**
   * Constructs a new {@link JSONRepresentationFactory}.
   *
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   */
  @Inject
  public JSONRepresentationFactory(Tracer tracer) {
    super(MediaType.APPLICATION_JSON_TYPE);

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

      Set<api.representations.json.Message> messages = new HashSet<>();
      for (Message message : notification.getMessages()) {
        messages.add(
            (api.representations.json.Message)
                this.createMessageRepresentation(null, null, message));
      }

      api.representations.json.NotificationStatus status =
          api.representations.json.NotificationStatus.valueOf(notification.getStatus().toString());

      api.representations.Representation representation =
          new api.representations.json.Notification.Builder()
              .uuid(notification.getUUID())
              .content(notification.getContent())
              .status(status)
              .sendAt(notification.getSendAt())
              .sentAt(notification.getSentAt())
              .targets(targets)
              .audiences(audiences)
              .messages(messages)
              .location(location)
              .language(language)
              .build();

      return representation;
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

  @Override
  public Representation createMessageRepresentation(
      URI location, Locale language, Message message) {
    Span span =
        this.tracer
            .buildSpan("JSONRepresentationFactory#createMessageRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      api.representations.json.MessageStatus status =
          api.representations.json.MessageStatus.valueOf(message.getStatus().toString());

      api.representations.Representation representation =
          new api.representations.json.Message.Builder()
              .id(message.getID())
              .from(message.getFrom())
              .to(message.getTo())
              .externalID(message.getExternalID())
              .content(message.getContent())
              .status(status)
              .location(location)
              .language(language)
              .build();

      return representation;
    } finally {
      span.finish();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param location {@inheritDoc}
   * @param language {@inheritDoc}
   * @param notifications {@inheritDoc}
   * @param skip {@inheritDoc}
   * @param take {@inheritDoc}
   * @param total {@inheritDoc}
   * @return {@inheritDoc}
   */
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
            .buildSpan("JSONRepresentationFactory#createNotificationCollectionRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      api.representations.RepresentationCollection.Builder builder =
          new api.representations.RepresentationCollection.Builder(this.getMediaType());
      for (Notification notification : notifications) {
        Representation notificationRepresentation =
            this.createNotificationRepresentation(location, language, notification);
        builder.add(notificationRepresentation);
      }
      return builder.total(total).build();
    } finally {
      span.finish();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param location {@inheritDoc}
   * @param language {@inheritDoc}
   * @param targets {@inheritDoc}
   * @param skip {@inheritDoc}
   * @param take {@inheritDoc}
   * @param total {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Representation createTargetCollectionRepresentation(
      URI location,
      Locale language,
      Set<Target> targets,
      Integer skip,
      Integer take,
      Integer total) {
    Span span =
        this.tracer
            .buildSpan("JSONRepresentationFactory#createTargetCollectionRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      api.representations.RepresentationCollection.Builder builder =
          new api.representations.RepresentationCollection.Builder(this.getMediaType());
      for (Target target : targets) {
        Representation targetRepresentation =
            this.createTargetRepresentation(location, language, target);
        builder.add(targetRepresentation);
      }
      return builder.total(total).build();
    } finally {
      span.finish();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param location {@inheritDoc}
   * @param language {@inheritDoc}
   * @param audiences {@inheritDoc}
   * @param skip {@inheritDoc}
   * @param take {@inheritDoc}
   * @param total {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Representation createAudienceCollectionRepresentation(
      URI location,
      Locale language,
      Set<Audience> audiences,
      Integer skip,
      Integer take,
      Integer total) {
    Span span =
        this.tracer
            .buildSpan("JSONRepresentationFactory#createAudienceCollectionRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      api.representations.RepresentationCollection.Builder builder =
          new api.representations.RepresentationCollection.Builder(this.getMediaType());
      for (Audience audience : audiences) {
        Representation audienceRepresentation =
            this.createAudienceRepresentation(location, language, audience);
        builder.add(audienceRepresentation);
      }
      return builder.total(total).build();
    } finally {
      span.finish();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param location {@inheritDoc}
   * @param language {@inheritDoc}
   * @param messages {@inheritDoc}
   * @param skip {@inheritDoc}
   * @param take {@inheritDoc}
   * @param total {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Representation createMessageCollectionRepresentation(
      URI location,
      Locale language,
      Set<Message> messages,
      Integer skip,
      Integer take,
      Integer total) {
    Span span =
        this.tracer
            .buildSpan("JSONRepresentationFactory#createMessageCollectionRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      api.representations.RepresentationCollection.Builder builder =
          new api.representations.RepresentationCollection.Builder(this.getMediaType());
      for (Message message : messages) {
        Representation messageRepresentation =
            this.createMessageRepresentation(location, language, message);
        builder.add(messageRepresentation);
      }
      return builder.total(total).build();
    } finally {
      span.finish();
    }
  }

  @Override
  public Representation createErrorRepresentation(
      URI location, Locale language, ApplicationException x) {
    String className = JSONRepresentationFactory.class.getName();
    String spanName = String.format("%s#createErrorRepresentation", className);
    Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return new Error(x);
    } finally {
      span.finish();
    }
  }
}
