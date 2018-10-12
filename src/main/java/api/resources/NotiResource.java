package api.resources;

import api.representations.RepresentationFactory;
import application.AudienceService;
import application.NotificationService;
import application.TargetService;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public final class NotiResource extends Resource implements api.NotiResource {

  private final NotificationService notificationService;
  private final AudienceService audienceService;
  private final TargetService targetService;

  @Inject
  public NotiResource(
      NotificationService notificationService,
      AudienceService audienceService,
      TargetService targetService,
      Map<MediaType, RepresentationFactory> representationIndustry,
      Tracer tracer) {
    super(representationIndustry, tracer);
    this.notificationService = notificationService;
    this.audienceService = audienceService;
    this.targetService = targetService;
  }

  @Override
  public Response get(HttpHeaders headers, UriInfo uriInfo) {
    String className = NotificationResource.class.getName();
    String spanName = String.format("%s#get", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      URI location = uriInfo.getRequestUri();
      Locale language = null;

      // retrieve the notification count.
      Integer totalNotifications = this.notificationService.getNotificationCount();

      // retrieve the audience count.
      Integer totalAudiences = this.audienceService.getAudienceCount();

      // retrieve the target count.
      Integer totalTargets = this.targetService.getTargetCount();

      api.representations.Representation representation = null;
      RepresentationFactory representationFactory = this.getRepresentationFactory(headers);
      representation =
          representationFactory.createNotiRepresentation(
              location, language, totalNotifications, totalAudiences, totalTargets);

      return Response.ok(representation).build();
    } finally {
      span.finish();
    }
  }
}
