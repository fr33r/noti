package api.representations.siren;

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
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import siren.Action;
import siren.EmbeddedLinkSubEntity;
import siren.Entity;
import siren.Field;
import siren.FieldType;
import siren.HttpMethod;
import siren.Link;
import siren.Relation;
import siren.factories.ActionBuilderFactory;
import siren.factories.EmbeddedLinkSubEntityBuilderFactory;
import siren.factories.EntityBuilderFactory;
import siren.factories.FieldBuilderFactory;
import siren.factories.LinkBuilderFactory;

/**
 * Defines the factory responsible for constructing {@code application/vnd.siren+json}
 * representations.
 *
 * @author Jon Freer
 */
public final class SirenRepresentationFactory extends RepresentationFactory {

  private final LinkBuilderFactory linkBuilderFactory;
  private final EntityBuilderFactory entityBuilderFactory;
  private final ActionBuilderFactory actionBuilderFactory;
  private final FieldBuilderFactory fieldBuilderFactory;
  private final EmbeddedLinkSubEntityBuilderFactory embeddedLinkSubEntityBuilderFactory;
  private final Tracer tracer;

  /**
   * Constructs a new {@link SirenRepresentationFactory}.
   *
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   */
  @Inject
  public SirenRepresentationFactory(Tracer tracer) {
    super(new MediaType("application", "vnd.siren+json"));

    // TODO - these should be injected.
    this.linkBuilderFactory = new LinkBuilderFactory();
    this.entityBuilderFactory = new EntityBuilderFactory();
    this.actionBuilderFactory = new ActionBuilderFactory();
    this.fieldBuilderFactory = new FieldBuilderFactory();
    this.embeddedLinkSubEntityBuilderFactory = new EmbeddedLinkSubEntityBuilderFactory();

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
            .buildSpan("SirenRepresentationFactory#createNotificationRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Link.Builder linkBuilder = this.linkBuilderFactory.create();
      Link self =
          linkBuilder
              .rel(Relation.SELF)
              .title("Self")
              .type(this.getMediaType().toString())
              .klass("notification")
              .href(location)
              .build();

      Action.Builder actionBuilder = this.actionBuilderFactory.create();
      Action delete =
          actionBuilder
              .name("delete-notification")
              .title("Delete Notification")
              .method(HttpMethod.DELETE)
              .href(location)
              .build();

      EmbeddedLinkSubEntity.Builder embeddedLinkSubEntityBuilder =
          this.embeddedLinkSubEntityBuilderFactory.create();

      Entity.Builder entityBuilder = this.entityBuilderFactory.create();

      // create target collection entity.
      EmbeddedLinkSubEntity targetCollectionSubEntity =
          embeddedLinkSubEntityBuilder
              .klasses("target", "collection")
              .title("Target Collection")
              .rel(Relation.COLLECTION)
              .type(this.getMediaType().toString())
              .href(
                  UriBuilder.fromUri(location)
                      .replacePath("/notifications/{uuid}/targets/")
                      .build(notification.getUUID()))
              .build();

      embeddedLinkSubEntityBuilder.clear();

      // create audience collection entity.
      EmbeddedLinkSubEntity audienceCollectionSubEntity =
          embeddedLinkSubEntityBuilder
              .klasses("audience", "collection")
              .title("Audience Collection")
              .rel(Relation.COLLECTION)
              .type(this.getMediaType().toString())
              .href(
                  UriBuilder.fromUri(location)
                      .replacePath("notifications/{uuid}/audiences/")
                      .build(notification.getUUID()))
              .build();

      embeddedLinkSubEntityBuilder.clear();

      EmbeddedLinkSubEntity messageCollectionSubEntity =
          embeddedLinkSubEntityBuilder
              .klasses("message", "collection")
              .title("Message Collection")
              .rel(Relation.COLLECTION)
              .type(this.getMediaType().toString())
              .href(
                  UriBuilder.fromUri(location)
                      .replacePath("notifications/{uuid}/messages/")
                      .build(notification.getUUID()))
              .build();

      Entity entity =
          entityBuilder
              .klass("notification")
              .property("uuid", notification.getUUID())
              .property("content", notification.getContent())
              .property("sendAt", notification.getSendAt())
              .property("sentAt", notification.getSentAt())
              .property("status", notification.getStatus().toString())
              .link(self)
              .actions(delete)
              .subEntities(
                  audienceCollectionSubEntity,
                  targetCollectionSubEntity,
                  messageCollectionSubEntity)
              .build();

      return new api.representations.siren.SirenEntityRepresentation.Builder()
          .entity(entity)
          .location(location)
          .language(language)
          .build();
    } catch (URISyntaxException x) {
      throw new RuntimeException(x);
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
            .buildSpan("SirenRepresentationFactory#createAudienceRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Link.Builder linkBuilder = this.linkBuilderFactory.create();
      Link self =
          linkBuilder
              .rel(Relation.SELF)
              .title("Self")
              .type(this.getMediaType().toString())
              .klass("audience")
              .href(location)
              .build();

      Action.Builder actionBuilder = this.actionBuilderFactory.create();
      Action delete =
          actionBuilder
              .name("delete-audience")
              .title("Delete Audience")
              .method(HttpMethod.DELETE)
              .href(location)
              .build();

      actionBuilder.clear();

      Field.Builder<String> stringFieldBuilder = this.fieldBuilderFactory.create();
      Field<String> uuidField =
          stringFieldBuilder
              .name("uuid")
              .title("Universally Unique Identifier")
              .type(FieldType.TEXT)
              .build();

      stringFieldBuilder.clear();

      Field<String> nameField =
          stringFieldBuilder.name("name").title("Name").type(FieldType.TEXT).build();

      Action replace =
          actionBuilder
              .name("replace-audience")
              .title("Replace Audience")
              .method(HttpMethod.PUT)
              .href(location)
              .fields(uuidField, nameField)
              .type(MediaType.APPLICATION_JSON)
              .build();

      EmbeddedLinkSubEntity.Builder embeddedLinkSubEntityBuilder =
          this.embeddedLinkSubEntityBuilderFactory.create();

      Entity.Builder entityBuilder = this.entityBuilderFactory.create();

      for (Target member : audience.getMembers()) {
        try {
          EmbeddedLinkSubEntity targetSubEntity =
              embeddedLinkSubEntityBuilder
                  .klass("target")
                  .title("Member")
                  .rel(Relation.ITEM)
                  .href(
                      UriBuilder.fromUri(location)
                          .replacePath("/targets/{uuid}/")
                          .build(member.getUUID()))
                  .build();
          entityBuilder.subEntity(targetSubEntity);
          embeddedLinkSubEntityBuilder.clear();
        } catch (URISyntaxException x) {
        }
      }

      Entity entity =
          entityBuilder
              .klass("audience")
              .property("uuid", audience.getUUID())
              .property("name", audience.getName())
              .link(self)
              .actions(delete, replace)
              .build();

      return new api.representations.siren.SirenEntityRepresentation.Builder()
          .entity(entity)
          .location(location)
          .language(language)
          .build();

    } catch (URISyntaxException x) {
      throw new RuntimeException(x);
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
            .buildSpan("SirenRepresentationFactory#createTargetRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Link.Builder linkBuilder = this.linkBuilderFactory.create();
      Link self =
          linkBuilder
              .rel(Relation.SELF)
              .title("Self")
              .type(this.getMediaType().toString())
              .klass("target")
              .href(location)
              .build();

      Action.Builder actionBuilder = this.actionBuilderFactory.create();
      Action delete =
          actionBuilder
              .name("delete-target")
              .title("Delete Target")
              .method(HttpMethod.DELETE)
              .href(location)
              .build();

      actionBuilder.clear();

      Field.Builder<String> stringFieldBuilder = this.fieldBuilderFactory.create();
      Field<String> uuidField =
          stringFieldBuilder
              .name("uuid")
              .title("Universally Unique Identifier")
              .type(FieldType.TEXT)
              .build();

      stringFieldBuilder.clear();

      Field<String> nameField =
          stringFieldBuilder.name("name").title("Name").type(FieldType.TEXT).build();

      stringFieldBuilder.clear();

      Field<String> phoneNumberField =
          stringFieldBuilder.name("phoneNumber").title("Phone Number").type(FieldType.TEL).build();

      Action replace =
          actionBuilder
              .name("replace-target")
              .title("Replace Target")
              .method(HttpMethod.PUT)
              .href(location)
              .fields(uuidField, nameField, phoneNumberField)
              .type(MediaType.APPLICATION_JSON)
              .build();

      Entity.Builder entityBuilder = this.entityBuilderFactory.create();
      Entity entity =
          entityBuilder
              .klass("target")
              .property("uuid", target.getUUID())
              .property("phoneNumber", target.getPhoneNumber())
              .property("name", target.getName())
              .link(self)
              .actions(delete, replace)
              .build();

      return new api.representations.siren.SirenEntityRepresentation.Builder()
          .entity(entity)
          .location(location)
          .language(language)
          .build();

    } catch (URISyntaxException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }

  @Override
  public Representation createMessageRepresentation(
      URI location, Locale language, Message message) {
    Span span =
        this.tracer
            .buildSpan("SirenRepresentationFactory#createMessageRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Link.Builder linkBuilder = this.linkBuilderFactory.create();
      Link self =
          linkBuilder
              .rel(Relation.SELF)
              .title("Self")
              .type(this.getMediaType().toString())
              .klass("message")
              .href(location)
              .build();

      Entity.Builder entityBuilder = this.entityBuilderFactory.create();
      Entity entity =
          entityBuilder
              .klass("message")
              .property("id", message.getID())
              .property("content", message.getContent())
              .property("to", message.getTo())
              .property("from", message.getFrom())
              .property("status", message.getStatus())
              .property("externalID", message.getExternalID())
              .link(self)
              .build();

      return new api.representations.siren.SirenEntityRepresentation.Builder()
          .entity(entity)
          .location(location)
          .language(language)
          .build();

    } catch (URISyntaxException x) {
      throw new RuntimeException(x);
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

    Representation representation = null;

    Span span =
        this.tracer
            .buildSpan("SirenRepresentationFactory#createNotificationCollectionRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      Link.Builder linkBuilder = this.linkBuilderFactory.create();
      Link self =
          linkBuilder
              .rel(Relation.SELF)
              .title("Self")
              .type(this.getMediaType().toString())
              .klass("notification")
              .href(location)
              .build();

      linkBuilder.clear();

      Field.Builder<String> stringFieldBuilder = this.fieldBuilderFactory.create();
      Field<String> contentField =
          stringFieldBuilder
              .name("content")
              .title("Notification Content")
              .type(FieldType.TEXT)
              .build();
      stringFieldBuilder.clear();

      Field.Builder<Date> dateFieldBuilder = this.fieldBuilderFactory.create();
      Field<Date> sendAtField =
          dateFieldBuilder
              .name("sendAt")
              .title("Notification Desired Send Date/Time")
              .type(FieldType.DATETIME)
              .build();
      dateFieldBuilder.clear();

      Action.Builder actionBuilder = this.actionBuilderFactory.create();
      Action create =
          actionBuilder
              .name("create-notification")
              .title("Create Notification")
              .type(MediaType.APPLICATION_JSON)
              .method(HttpMethod.POST)
              .href(location)
              .fields(contentField, sendAtField)
              .build();

      EmbeddedLinkSubEntity.Builder embeddedLinkSubEntityBuilder =
          this.embeddedLinkSubEntityBuilderFactory.create();

      Entity.Builder entityBuilder = this.entityBuilderFactory.create();

      boolean hasPreviousLink = this.hasPreviousLink(skip, take, total);
      boolean hasNextLink = this.hasNextLink(skip, take, total);

      if (hasPreviousLink) {
        int prevSkip = skip - take >= 0 ? skip - take : 0;
        int prevTake = skip - prevSkip < take ? skip - prevSkip : take;

        URI prevHref =
            UriBuilder.fromUri(location)
                .replaceQueryParam("skip", prevSkip)
                .replaceQueryParam("take", prevTake)
                .build();

        Link prevLink =
            linkBuilder
                .rel(Relation.PREV)
                .title("previous")
                .type(this.getMediaType().toString())
                .href(prevHref)
                .build();

        entityBuilder.link(prevLink);
        linkBuilder.clear();
      }

      if (hasNextLink) {
        int nextSkip = skip + take;
        int nextTake = take;

        URI nextHref =
            UriBuilder.fromUri(location)
                .replaceQueryParam("skip", nextSkip)
                .replaceQueryParam("take", nextTake)
                .build();

        Link nextLink =
            linkBuilder
                .rel(Relation.NEXT)
                .title("next")
                .type(this.getMediaType().toString())
                .href(nextHref)
                .build();

        entityBuilder.link(nextLink);
        linkBuilder.clear();
      }

      for (Notification notification : notifications) {
        EmbeddedLinkSubEntity notificationSubEntity =
            embeddedLinkSubEntityBuilder
                .klass("notification")
                .title("Notification")
                .rel(Relation.ITEM)
                .type(this.getMediaType().toString())
                .href(
                    UriBuilder.fromUri(location)
                        .replacePath("/notifications/{uuid}/")
                        .build(notification.getUUID()))
                .build();
        entityBuilder.subEntity(notificationSubEntity);
        embeddedLinkSubEntityBuilder.clear();
      }

      Entity entity =
          entityBuilder
              .klass("notification")
              .klass("collection")
              .property("total", total)
              .link(self)
              .actions(create)
              .build();

      representation =
          new api.representations.siren.SirenEntityRepresentation.Builder()
              .entity(entity)
              .location(location)
              .language(language)
              .build();

    } catch (URISyntaxException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }

    return representation;
  }

  @Override
  public Representation createTargetCollectionRepresentation(
      URI location,
      Locale language,
      Set<Target> targets,
      Integer skip,
      Integer take,
      Integer total) {

    Representation representation = null;

    Span span =
        this.tracer
            .buildSpan("SirenRepresentationFactory#createTargetCollectionRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      Link.Builder linkBuilder = this.linkBuilderFactory.create();
      Link self =
          linkBuilder
              .rel(Relation.SELF)
              .title("Self")
              .type(this.getMediaType().toString())
              .klass("target")
              .href(location)
              .build();

      linkBuilder.clear();

      EmbeddedLinkSubEntity.Builder embeddedLinkSubEntityBuilder =
          this.embeddedLinkSubEntityBuilderFactory.create();

      Entity.Builder entityBuilder = this.entityBuilderFactory.create();

      boolean hasPreviousLink = this.hasPreviousLink(skip, take, total);
      boolean hasNextLink = this.hasNextLink(skip, take, total);

      if (hasPreviousLink) {
        int prevSkip = skip - take >= 0 ? skip - take : 0;
        int prevTake = skip - prevSkip < take ? skip - prevSkip : take;

        URI prevHref =
            UriBuilder.fromUri(location)
                .replaceQueryParam("skip", prevSkip)
                .replaceQueryParam("take", prevTake)
                .build();

        Link prevLink =
            linkBuilder
                .rel(Relation.PREV)
                .title("previous")
                .type(this.getMediaType().toString())
                .href(prevHref)
                .build();

        entityBuilder.link(prevLink);
        linkBuilder.clear();
      }

      if (hasNextLink) {
        int nextSkip = skip + take;
        int nextTake = take;

        URI nextHref =
            UriBuilder.fromUri(location)
                .replaceQueryParam("skip", nextSkip)
                .replaceQueryParam("take", nextTake)
                .build();

        Link nextLink =
            linkBuilder
                .rel(Relation.NEXT)
                .title("next")
                .type(this.getMediaType().toString())
                .href(nextHref)
                .build();

        entityBuilder.link(nextLink);
        linkBuilder.clear();
      }

      for (Target target : targets) {
        EmbeddedLinkSubEntity targetSubEntity =
            embeddedLinkSubEntityBuilder
                .klass("target")
                .title("Target")
                .rel(Relation.ITEM)
                .type(this.getMediaType().toString())
                .href(
                    UriBuilder.fromUri(location)
                        .replacePath("/targets/{uuid}/")
                        .build(target.getUUID()))
                .build();
        entityBuilder.subEntity(targetSubEntity);
        embeddedLinkSubEntityBuilder.clear();
      }

      Entity entity =
          entityBuilder
              .klass("target")
              .klass("collection")
              .property("total", total)
              .link(self)
              .build();

      representation =
          new api.representations.siren.SirenEntityRepresentation.Builder()
              .entity(entity)
              .location(location)
              .language(language)
              .build();

    } catch (URISyntaxException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }

    return representation;
  }

  @Override
  public Representation createAudienceCollectionRepresentation(
      URI location,
      Locale language,
      Set<Audience> audiences,
      Integer skip,
      Integer take,
      Integer total) {

    Representation representation = null;

    Span span =
        this.tracer
            .buildSpan("SirenRepresentationFactory#createAudienceCollectionRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      Link.Builder linkBuilder = this.linkBuilderFactory.create();
      Link self =
          linkBuilder
              .rel(Relation.SELF)
              .title("Self")
              .type(this.getMediaType().toString())
              .klass("audience")
              .href(location)
              .build();

      linkBuilder.clear();

      EmbeddedLinkSubEntity.Builder embeddedLinkSubEntityBuilder =
          this.embeddedLinkSubEntityBuilderFactory.create();

      Entity.Builder entityBuilder = this.entityBuilderFactory.create();

      boolean hasPreviousLink = this.hasPreviousLink(skip, take, total);
      boolean hasNextLink = this.hasNextLink(skip, take, total);

      if (hasPreviousLink) {
        int prevSkip = skip - take >= 0 ? skip - take : 0;
        int prevTake = skip - prevSkip < take ? skip - prevSkip : take;

        URI prevHref =
            UriBuilder.fromUri(location)
                .replaceQueryParam("skip", prevSkip)
                .replaceQueryParam("take", prevTake)
                .build();

        Link prevLink =
            linkBuilder
                .rel(Relation.PREV)
                .title("previous")
                .type(this.getMediaType().toString())
                .href(prevHref)
                .build();

        entityBuilder.link(prevLink);
        linkBuilder.clear();
      }

      if (hasNextLink) {
        int nextSkip = skip + take;
        int nextTake = take;

        URI nextHref =
            UriBuilder.fromUri(location)
                .replaceQueryParam("skip", nextSkip)
                .replaceQueryParam("take", nextTake)
                .build();

        Link nextLink =
            linkBuilder
                .rel(Relation.NEXT)
                .title("next")
                .type(this.getMediaType().toString())
                .href(nextHref)
                .build();

        entityBuilder.link(nextLink);
        linkBuilder.clear();
      }

      for (Audience audience : audiences) {
        EmbeddedLinkSubEntity audienceSubEntity =
            embeddedLinkSubEntityBuilder
                .klass("audience")
                .title("Audience")
                .rel(Relation.ITEM)
                .type(this.getMediaType().toString())
                .href(
                    UriBuilder.fromUri(location)
                        .replacePath("/audiences/{uuid}/")
                        .build(audience.getUUID()))
                .build();
        entityBuilder.subEntity(audienceSubEntity);
        embeddedLinkSubEntityBuilder.clear();
      }

      Entity entity =
          entityBuilder
              .klass("audience")
              .klass("collection")
              .property("total", total)
              .link(self)
              .build();

      representation =
          new api.representations.siren.SirenEntityRepresentation.Builder()
              .entity(entity)
              .location(location)
              .language(language)
              .build();

    } catch (URISyntaxException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }

    return representation;
  }

  @Override
  public Representation createMessageCollectionRepresentation(
      URI location,
      Locale language,
      Set<Message> messages,
      Integer skip,
      Integer take,
      Integer total) {

    Representation representation = null;

    Span span =
        this.tracer
            .buildSpan("SirenRepresentationFactory#createMessageCollectionRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      Link.Builder linkBuilder = this.linkBuilderFactory.create();
      Link self =
          linkBuilder
              .rel(Relation.SELF)
              .title("Self")
              .type(this.getMediaType().toString())
              .klass("message")
              .href(location)
              .build();

      linkBuilder.clear();

      EmbeddedLinkSubEntity.Builder embeddedLinkSubEntityBuilder =
          this.embeddedLinkSubEntityBuilderFactory.create();

      Entity.Builder entityBuilder = this.entityBuilderFactory.create();

      boolean hasPreviousLink = this.hasPreviousLink(skip, take, total);
      boolean hasNextLink = this.hasNextLink(skip, take, total);

      if (hasPreviousLink) {
        int prevSkip = skip - take >= 0 ? skip - take : 0;
        int prevTake = skip - prevSkip < take ? skip - prevSkip : take;

        URI prevHref =
            UriBuilder.fromUri(location)
                .replaceQueryParam("skip", prevSkip)
                .replaceQueryParam("take", prevTake)
                .build();

        Link prevLink =
            linkBuilder
                .rel(Relation.PREV)
                .title("previous")
                .type(this.getMediaType().toString())
                .href(prevHref)
                .build();

        entityBuilder.link(prevLink);
        linkBuilder.clear();
      }

      if (hasNextLink) {
        int nextSkip = skip + take;
        int nextTake = take;

        URI nextHref =
            UriBuilder.fromUri(location)
                .replaceQueryParam("skip", nextSkip)
                .replaceQueryParam("take", nextTake)
                .build();

        Link nextLink =
            linkBuilder
                .rel(Relation.NEXT)
                .title("next")
                .type(this.getMediaType().toString())
                .href(nextHref)
                .build();

        entityBuilder.link(nextLink);
        linkBuilder.clear();
      }

      for (Message message : messages) {

        EmbeddedLinkSubEntity messageSubEntity =
            embeddedLinkSubEntityBuilder
                .klass("message")
                .title("Message")
                .rel(Relation.ITEM)
                .type(this.getMediaType().toString())
                .href(UriBuilder.fromUri(location).path("/{id}/").build(message.getID()))
                .build();
        entityBuilder.subEntity(messageSubEntity);
        embeddedLinkSubEntityBuilder.clear();
      }

      Entity entity =
          entityBuilder
              .klass("message")
              .klass("collection")
              .property("total", total)
              .link(self)
              .build();

      representation =
          new api.representations.siren.SirenEntityRepresentation.Builder()
              .entity(entity)
              .location(location)
              .language(language)
              .build();

    } catch (URISyntaxException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }

    return representation;
  }

  @Override
  public Representation createErrorRepresentation(
      URI location, Locale language, ApplicationException x) {
    String className = SirenRepresentationFactory.class.getName();
    String spanName = String.format("%s#createErrorRepresentation", className);
    Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Entity.Builder entityBuilder = this.entityBuilderFactory.create();
      Entity entity =
          entityBuilder
              .klass("error")
              .property("message", x.getMessage())
              .property("detailedMessage", x.getDetailedMessage())
              .property("sillyMessage", x.getSillyMessage())
              .property("emoji", x.getEmoji())
              .build();

      Representation representation =
          new SirenEntityRepresentation.Builder().entity(entity).language(language).build();
      return representation;
    } finally {
      span.finish();
    }
  }

  private boolean hasPreviousLink(Integer skip, Integer take, Integer total) {
    boolean hasPrevious = false;
    if (take != null && skip != null) {
      hasPrevious = skip > 0;
    }
    return hasPrevious;
  }

  private boolean hasNextLink(Integer skip, Integer take, Integer total) {
    boolean hasNext = false;
    if (take != null) {
      hasNext = total - take > 0;
      if (skip != null) {
        hasNext = total - (skip + take) > 0;
      }
    }
    return hasNext;
  }
}
