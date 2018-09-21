package api.resources;

import api.representations.Representation;
import api.representations.RepresentationFactory;
import api.representations.json.Target;
import application.TargetFactory;
import application.TargetService;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
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
public final class TargetResource extends Resource implements api.TargetResource {

  private final TargetService targetService;
  private final TargetFactory targetFactory;

  /**
   * Construct a new {@link TargetResource}.
   *
   * @param representationIndustry The collection of representation factories used to construct
   *     representations.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   * @param targetService The application service that orchestrates various operations with targets.
   */
  @Inject
  public TargetResource(
      TargetService targetService,
      Map<MediaType, RepresentationFactory> representationIndustry,
      Tracer tracer) {
    super(representationIndustry, tracer);
    this.targetService = targetService;
    this.targetFactory = new TargetFactory();
  }

  /**
   * {@inheritDoc}
   *
   * @param headers {@inheritDoc}
   * @param uriInfo {@inheritDoc}
   * @param uuid {@inheritDoc}
   */
  public Response get(HttpHeaders headers, UriInfo uriInfo, String uuid) {
    String className = TargetResource.class.getName();
    String spanName = String.format("%s#get", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      URI location = uriInfo.getRequestUri();
      Locale language = null;
      application.Target target = this.targetService.getTarget(UUID.fromString(uuid));

      RepresentationFactory representationFactory = this.getRepresentationFactory(headers);
      Representation representation =
          representationFactory.createTargetRepresentation(location, language, target);
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
   * @param target {@inheritDoc}
   * @return {@inheritDoc}
   */
  public Response createAndAppend(HttpHeaders headers, UriInfo uriInfo, Target target) {
    String className = TargetResource.class.getName();
    String spanName = String.format("%s#createAndAppend", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      UUID uuid = this.targetService.createTarget(this.targetFactory.createFrom(target));
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
   * @param target {@inheritDoc}
   * @return {@inheritDoc}
   */
  public Response replace(HttpHeaders headers, UriInfo uriInfo, Target target) {
    String className = TargetResource.class.getName();
    String spanName = String.format("%s#replace", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      this.targetService.replaceTarget(this.targetFactory.createFrom(target));
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
  public Response delete(UriInfo uriInfo, String uuid) {
    String className = TargetResource.class.getName();
    String spanName = String.format("%s#delete", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      this.targetService.deleteTarget(UUID.fromString(uuid));
      return Response.noContent().build();
    } finally {
      span.finish();
    }
  }
}
