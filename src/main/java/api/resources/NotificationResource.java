package api.resources;

import api.representations.RepresentationFactory;
import api.representations.json.Notification;
import application.AudienceFactory;
import application.MessageFactory;
import application.NotificationFactory;
import application.NotificationService;
import application.Target;
import application.TargetFactory;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;
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
public class NotificationResource extends Resource implements api.NotificationResource {

  private final NotificationService notificationService;
  private final NotificationFactory notificationFactory;

  /**
   * Construct a new {@link NotificationResource}.
   *
   * @param representationIndustry The collection of representation factories used to construct
   *     representations.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   * @param notificationService The application service that orchestrates various operations with
   *     notifications.
   */
  @Inject
  public NotificationResource(
      NotificationService notificationService,
      Map<MediaType, RepresentationFactory> representationIndustry,
      Tracer tracer) {
    super(representationIndustry, tracer);
    this.notificationService = notificationService;
    this.notificationFactory =
        new NotificationFactory(
            new TargetFactory(), new AudienceFactory(new TargetFactory()), new MessageFactory());
  }

  /**
   * {@inheritDoc}
   *
   * @param headers {@inheritDoc}
   * @param uriInfo {@inheritDoc}
   * @param messageExternalID {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response getCollection(
      HttpHeaders headers, UriInfo uriInfo, String messageExternalID, Integer skip, Integer take) {
    String className = NotificationResource.class.getName();
    String spanName = String.format("%s#getCollection", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      URI location = uriInfo.getRequestUri();
      Locale language = null;

      Set<application.Notification> notifications =
          this.notificationService.getNotifications(messageExternalID, skip, take);
      Integer total = this.notificationService.getNotificationCount();

      api.representations.Representation representation = null;
      RepresentationFactory representationFactory = this.getRepresentationFactory(headers);
      representation =
          representationFactory.createNotificationCollectionRepresentation(
              location, language, notifications, skip, take, total);

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
   * @param uuid {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response get(HttpHeaders headers, UriInfo uriInfo, String uuid) {
    String className = NotificationResource.class.getName();
    String spanName = String.format("%s#get", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      URI requestURI = uriInfo.getRequestUri();
      Locale language = null;
      application.Notification notification =
          this.notificationService.getNotification(UUID.fromString(uuid));

      api.representations.Representation representation = null;
      RepresentationFactory representationFactory = this.getRepresentationFactory(headers);
      representation =
          representationFactory.createNotificationRepresentation(
              requestURI, language, notification);
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
    String className = NotificationResource.class.getName();
    String spanName = String.format("%s#createAndAppend", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
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
    String className = NotificationResource.class.getName();
    String spanName = String.format("%s#replace", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
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
    String className = NotificationResource.class.getName();
    String spanName = String.format("%s#delete", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      this.notificationService.deleteNotification(UUID.fromString(uuid));
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
  public Response getTargetCollection(
      HttpHeaders headers, UriInfo uriInfo, String uuid, Integer skip, Integer take) {
    String className = NotificationResource.class.getName();
    String spanName = String.format("%s#getTargetCollection", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      URI requestURI = uriInfo.getRequestUri();
      Locale language = null;
      Set<Target> notificationTargets =
          this.notificationService.getNotificationDirectRecipients(
              UUID.fromString(uuid), skip, take);
      application.Notification notification =
          this.notificationService.getNotification(UUID.fromString(uuid));
      int totalTargets = notification.getTargets().size();
      api.representations.Representation representation = null;
      RepresentationFactory representationFactory = this.getRepresentationFactory(headers);
      representation =
          representationFactory.createTargetCollectionRepresentation(
              requestURI, language, notificationTargets, skip, take, totalTargets);
      return Response.ok(representation).build();
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
  public Response getAudienceCollection(
      HttpHeaders headers, UriInfo uriInfo, String uuid, Integer skip, Integer take) {
    String className = NotificationResource.class.getName();
    String spanName = String.format("%s#getAudienceCollection", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      URI requestURI = uriInfo.getRequestUri();
      Locale language = null;
      application.Notification notification =
          this.notificationService.getNotification(UUID.fromString(uuid));
      int audiencesTotal = notification.getAudiences().size();
      Set<application.Audience> audiences =
          this.notificationService.getNotificationAudiences(UUID.fromString(uuid), skip, take);
      api.representations.Representation representation = null;
      RepresentationFactory representationFactory = this.getRepresentationFactory(headers);
      representation =
          representationFactory.createAudienceCollectionRepresentation(
              requestURI, language, audiences, skip, take, audiencesTotal);
      return Response.ok(representation).build();
    } finally {
      span.finish();
    }
  }
}
