package api.resources;

import api.representations.RepresentationFactory;
import api.representations.json.Message;
import application.MessageFactory;
import application.Notification;
import application.NotificationService;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * {@inheritDoc}
 *
 * @author Jon Freer
 */
public final class MessageResource implements api.MessageResource {

  private final NotificationService notificationService;
  private final Tracer tracer;
  private final MessageFactory messageFactory;
  private final Map<MediaType, RepresentationFactory> representationIndustry;

  @Inject
  public MessageResource(
      NotificationService notificationService,
      @Named("JSONRepresentationFactory") RepresentationFactory jsonRepresentationFactory,
      @Named("XMLRepresentationFactory") RepresentationFactory xmlRepresentationFactory,
      @Named("SirenRepresentationFactory") RepresentationFactory sirenRepresentationFactory,
      MessageFactory messageFactory,
      Tracer tracer) {

    this.notificationService = notificationService;
    this.messageFactory = messageFactory;
    this.representationIndustry = new HashMap<>();
    this.representationIndustry.put(
        jsonRepresentationFactory.getMediaType(), jsonRepresentationFactory);
    this.representationIndustry.put(
        xmlRepresentationFactory.getMediaType(), xmlRepresentationFactory);
    this.representationIndustry.put(
        sirenRepresentationFactory.getMediaType(), sirenRepresentationFactory);
    this.tracer = tracer;
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
    Span span = this.tracer.buildSpan(spanName).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      URI requestURI = uriInfo.getRequestUri();
      Locale language = null;
      application.Notification notification =
          this.notificationService.getNotification(UUID.fromString(uuid));
      int messagesTotal = notification.getMessages().size();
      Set<application.Message> messages =
          this.notificationService.getNotificationMessages(UUID.fromString(uuid), skip, take);
      api.representations.Representation representation = null;
      for (MediaType acceptableMediaType : headers.getAcceptableMediaTypes()) {
        if (this.representationIndustry.containsKey(acceptableMediaType)) {
          RepresentationFactory representationFactory =
              this.representationIndustry.get(acceptableMediaType);
          representation =
              representationFactory.createMessageCollectionRepresentation(
                  requestURI, language, messages, skip, take, messagesTotal);
        }
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
   * @param notificationUUID {@inheritDoc}
   * @param id {@inheritDoc}
   * @return {@inheritDoc}
   */
  public Response get(HttpHeaders headers, UriInfo uriInfo, String notificationUUID, Integer id) {
    String className = this.getClass().getName();
    String spanName = String.format("%s#get", className);
    Span span = this.tracer.buildSpan(spanName).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
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
      api.representations.Representation representation = null;
      for (MediaType acceptableMediaType : headers.getAcceptableMediaTypes()) {
        if (this.representationIndustry.containsKey(acceptableMediaType)) {
          RepresentationFactory representationFactory =
              this.representationIndustry.get(acceptableMediaType);
          representation =
              representationFactory.createMessageRepresentation(location, language, message);
        }
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
   * @param notificationUUID {@inheritDoc}
   * @param message {@inheritDoc}
   * @return {@inheritDoc}
   */
  public Response replace(
      HttpHeaders headers, UriInfo uriInfo, String notificationUUID, Message message) {
    String className = this.getClass().getName();
    String spanName = String.format("%s#replace", className);
    Span span = this.tracer.buildSpan(spanName).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      this.notificationService.updateNotificationMessage(
          UUID.fromString(notificationUUID), this.messageFactory.createFrom(message));
      return Response.noContent().build();
    } finally {
      span.finish();
    }
  }
}
