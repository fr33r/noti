package api.resources;

import api.representations.RepresentationFactory;
import api.representations.json.Notification;
import application.AudienceFactory;
import application.NotificationFactory;
import application.NotificationService;
import application.TargetFactory;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.net.URI;
import java.util.Locale;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * {@inheritDoc}
 *
 * @author jonfreer
 */
public class NotificationResource implements api.NotificationResource {

  private final NotificationService notificationService;
  private final RepresentationFactory jsonRepresentationFactory;
  private final RepresentationFactory xmlRepresentationFactory;
  private final RepresentationFactory sirenRepresentationFactory;
  private final NotificationFactory notificationFactory;
  private final Tracer tracer;

  /**
   * Construct a new {@link NotificationResource}.
   *
   * @param jsonRepresentationFactory The {@link api.representations.json.JSONRepresentationFactory}
   *     responsible for constructing JSON {@link api.representations.Representation} instances.
   * @param xmlRepresentationFactory The {@link api.representations.xml.XMLRepresentationFactory}
   *     responsible for constructing XML {@link api.representations.Representation} instances.
   * @param sirenRepresentationFactory The {@link
   *     api.representations.siren.SirenRepresentationFactory} responsible for constructing Siren
   *     {@link api.representations.Representation} instances.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   * @param notificationService The application service that orchestrates various operations with
   *     notifications.
   */
  @Inject
  public NotificationResource(
      NotificationService notificationService,
      @Named("JSONRepresentationFactory") RepresentationFactory jsonRepresentationFactory,
      @Named("XMLRepresentationFactory") RepresentationFactory xmlRepresentationFactory,
      @Named("SirenRepresentationFactory") RepresentationFactory sirenRepresentationFactory,
      Tracer tracer) {
    this.notificationService = notificationService;
    this.jsonRepresentationFactory = jsonRepresentationFactory;
    this.xmlRepresentationFactory = xmlRepresentationFactory;
    this.sirenRepresentationFactory = sirenRepresentationFactory;
    this.tracer = tracer;
    this.notificationFactory =
        new NotificationFactory(new TargetFactory(), new AudienceFactory(new TargetFactory()));
  }

  /**
   * {@inheritDoc}
   *
   * @param headers {@inheritDoc}
   * @param uriInfo {@inheritDoc}
   * @param uuid {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response get(HttpHeaders headers, UriInfo uriInfo, String uuid) {
    Span span = this.tracer.buildSpan("NotificationResource#get").start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      URI requestURI = uriInfo.getRequestUri();

      // as currently designed, the notification is stored agnostic to which
      // language audience it is intended for. Due to this, NOTI cannot
      // perform content negotiation on the language, since a language is not
      // associated with the notification content. if/when intended language
      // audiences are assocaited with notification content, can NOTI
      // fully support content negotiation based on Accept-Language.
      //
      // as outlined in RFC7231 section 5.3.5, the origin server is free
      // to ignore the Accept-Language header when performing content negotation.
      // the RFC does not recommend returning 406, since it is possible for
      // clients to translate.
      Locale language = null;
      application.Notification notification =
          this.notificationService.getNotification(UUID.fromString(uuid));

      // TODO - change the following conditional blocks to honer q-value hierarchy.
      api.representations.Representation representation;
      if (headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_XML_TYPE)) {
        representation =
            this.xmlRepresentationFactory.createNotificationRepresentation(
                requestURI, language, notification);
      } else if (headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_JSON_TYPE)) {
        representation =
            this.jsonRepresentationFactory.createNotificationRepresentation(
                requestURI, language, notification);
      } else {
        representation =
            this.sirenRepresentationFactory.createNotificationRepresentation(
                requestURI, language, notification);
      }
      return Response.ok(representation).build();
    } finally {
      span.finish();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param headers {@inheritDoc}
   * @param uriInfo {@inheritDoc}
   * @param notification {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response createAndAppend(HttpHeaders headers, UriInfo uriInfo, Notification notification) {
    Span span = this.tracer.buildSpan("NotificationResource#createAndAppend").start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      UUID uuid =
          this.notificationService.createNotification(
              this.notificationFactory.createFrom(notification));
      URI location =
          UriBuilder.fromUri(uriInfo.getRequestUri()).path("/{uuid}/").build(uuid.toString());
      return Response.created(location).build();
    } finally {
      span.finish();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param headers {@inheritDoc}
   * @param uriInfo {@inheritDoc}
   * @param notification {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response replace(HttpHeaders headers, UriInfo uriInfo, Notification notification) {
    Span span = this.tracer.buildSpan("NotificationResource#replace").start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      this.notificationService.updateNotification(
          this.notificationFactory.createFrom(notification));
      return Response.noContent().build();
    } finally {
      span.finish();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param uuid {@inheritDoc}
   * @param uriInfo {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response delete(UriInfo uriInfo, String uuid) {
    Span span = this.tracer.buildSpan("NotificationResource#delete").start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      this.notificationService.deleteNotification(UUID.fromString(uuid));
      return Response.noContent().build();
    } finally {
      span.finish();
    }
  }
}
