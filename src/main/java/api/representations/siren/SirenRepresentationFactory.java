package api.representations.siren;

import api.representations.Representation;
import api.representations.RepresentationFactory;
import application.Audience;
import application.Notification;
import application.Target;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
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
 * @auther Jon Freer
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

      Link self = null;
      try {
        self = linkBuilder.rel(Relation.SELF).href(location).build();
      } catch (URISyntaxException x) {
      }

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
      EmbeddedLinkSubEntity targetCollectionSubEntity = null;
      EmbeddedLinkSubEntity audienceCollectionSubEntity = null;

      try {
        // create target collection entity.
        targetCollectionSubEntity =
            embeddedLinkSubEntityBuilder
                .klasses("target", "collection")
                .title("Target Collection")
                .rel(Relation.COLLECTION)
                .href(UriBuilder.fromUri(location).replacePath("/targets/").build())
                .build();
      } catch (URISyntaxException x) {
      }

      embeddedLinkSubEntityBuilder.clear();

      try {
        // create audience collection entity.
        audienceCollectionSubEntity =
            embeddedLinkSubEntityBuilder
                .klasses("audience", "collection")
                .title("Audience Collection")
                .rel(Relation.COLLECTION)
                .href(UriBuilder.fromUri(location).replacePath("/audiences/").build())
                .build();
      } catch (URISyntaxException x) {
      }

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
              .subEntities(audienceCollectionSubEntity, targetCollectionSubEntity)
              .build();

      return new api.representations.siren.SirenEntityRepresentation.Builder()
          .entity(entity)
          .location(location)
          .language(language)
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
            .buildSpan("SirenRepresentationFactory#createAudienceRepresentation")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Link.Builder linkBuilder = this.linkBuilderFactory.create();

      Link self = null;
      try {
        self = linkBuilder.rel(Relation.SELF).href(location).build();
      } catch (URISyntaxException x) {
      }

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

      Link self = null;
      try {
        self = linkBuilder.rel(Relation.SELF).href(location).build();
      } catch (URISyntaxException x) {
      }

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

    } finally {
      span.finish();
    }
  }
}
