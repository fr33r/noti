package api.resources;

import api.representations.Representation;
import api.representations.RepresentationFactory;
import application.Message;
import application.MessageFactory;
import application.Notification;
import application.NotificationService;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;

/**
 * {@inheritDoc}
 *
 * @author Jon Freer
 */
public final class MessageResource extends Resource implements api.MessageResource {

  private final NotificationService notificationService;
  private final MessageFactory messageFactory;
  private final Logger logger;

  @Inject
  public MessageResource(
      NotificationService notificationService,
      Map<MediaType, RepresentationFactory> representationIndustry,
      MessageFactory messageFactory,
      Tracer tracer,
      Logger logger) {
    super(representationIndustry, tracer);
    this.notificationService = notificationService;
    this.messageFactory = messageFactory;
    this.logger = logger;
  }

  /**
   * {@inheritDoc}
   *
   * @param headers {@inheritDoc}
   * @param uuid {@inheritDoc}
   * @param uriInfo {@inheritDoc}
   * @param skip {@inheritDoc}
   * @param take {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response getCollection(
      HttpHeaders headers, UriInfo uriInfo, String uuid, Integer skip, Integer take) {
    String className = this.getClass().getName();
    String spanName = String.format("%s#getCollection", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      URI location = uriInfo.getRequestUri();
      Locale language = null;
      application.Notification notification =
          this.notificationService.getNotification(UUID.fromString(uuid));
      int messagesTotal = notification.getMessages().size();
      Set<application.Message> messages =
          this.notificationService.getNotificationMessages(UUID.fromString(uuid), skip, take);

      RepresentationFactory representationFactory = this.getRepresentationFactory(headers);
      Representation representation =
          representationFactory.createMessageCollectionRepresentation(
              location, language, messages, skip, take, messagesTotal);
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
   * @param notificationUUID {@inheritDoc}
   * @param id {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response get(HttpHeaders headers, UriInfo uriInfo, String notificationUUID, Integer id) {
    String className = this.getClass().getName();
    String spanName = String.format("%s#get", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      URI location = uriInfo.getRequestUri();
      Locale language = null;
      Notification notification =
          this.notificationService.getNotification(UUID.fromString(notificationUUID));
      application.Message message = null;
      for (application.Message m : notification.getMessages()) {
        if (m.getID().equals(id)) {
          message = m;
          break;
        }
      }

      RepresentationFactory representationFactory = this.getRepresentationFactory(headers);
      Representation representation =
          representationFactory.createMessageRepresentation(location, language, message);
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
   * @param notificationUUID {@inheritDoc}
   * @param message {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response replace(
      HttpHeaders headers,
      UriInfo uriInfo,
      String notificationUUID,
      api.representations.json.Message message) {
    String className = this.getClass().getName();
    String spanName = String.format("%s#replace", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      this.notificationService.updateNotificationMessage(
          UUID.fromString(notificationUUID), this.messageFactory.createFrom(message));
      return Response.noContent().build();
    } finally {
      span.finish();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param headers {@inheritDoc}
   * @param uriInfo {@inheritDoc}
   * @param notificationUUID {@inheritDoc}
   * @param message {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response replace(
      HttpHeaders headers,
      UriInfo uriInfo,
      String notificationUUID,
      api.representations.xml.Message message) {
    String className = this.getClass().getName();
    String spanName = String.format("%s#replace", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      this.notificationService.updateNotificationMessage(
          UUID.fromString(notificationUUID), this.messageFactory.createFrom(message));
      return Response.noContent().build();
    } finally {
      span.finish();
    }
  }

  @Override
  public Response createAndAppend(
      HttpHeaders headers,
      UriInfo uriInfo,
      String notificationUUID,
      Integer id,
      MultivaluedMap<String, String> messageLog) {

    String className = this.getClass().getName();
    String spanName = String.format("%s#createAndAppend", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      URI location = uriInfo.getRequestUri();
      Locale language = null;
      Integer skip = null;
      Integer take = null;
      UUID nUUID = UUID.fromString(notificationUUID);

      Message message = this.notificationService.getNotificationMessage(nUUID, id);

      if (message == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      // apply changes.
      Message updatedMessage = this.messageFactory.createFrom(messageLog, message);
      this.notificationService.updateNotificationMessage(nUUID, updatedMessage);
      return Response.noContent().build();
    } finally {
      span.finish();
    }
  }
}
