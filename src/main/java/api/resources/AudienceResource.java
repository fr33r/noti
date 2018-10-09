package api.resources;

import api.representations.Representation;
import api.representations.RepresentationFactory;
import application.AudienceFactory;
import application.AudienceService;
import application.TargetFactory;
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

public class AudienceResource extends Resource implements api.AudienceResource {

  private final AudienceService audienceService;
  private final AudienceFactory audienceFactory;

  /**
   * Construct a new {@link AudienceResource}.
   *
   * @param representationIndustry The collection of representation factories used to construct
   *     representations.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   * @param audienceService The application service that orchestrates various operations with
   *     audiences.
   */
  @Inject
  public AudienceResource(
      AudienceService audienceService,
      Map<MediaType, RepresentationFactory> representationIndustry,
      Tracer tracer) {
    super(representationIndustry, tracer);
    this.audienceService = audienceService;
    this.audienceFactory = new AudienceFactory(new TargetFactory());
  }

  /**
   * {@inheritDoc}
   *
   * @param headers {@inheritDoc}
   * @param uriInfo {@inheritDoc}
   * @param uuid {@inheritDoc}
   */
  @Override
  public Response get(HttpHeaders headers, UriInfo uriInfo, String uuid) {
    String className = TargetResource.class.getName();
    String spanName = String.format("%s#get", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      URI location = uriInfo.getRequestUri();
      Locale language = null;
      application.Audience audience = this.audienceService.getAudience(UUID.fromString(uuid));

      RepresentationFactory representationFactory = this.getRepresentationFactory(headers);
      Representation representation =
          representationFactory.createAudienceRepresentation(location, language, audience);
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
   * @param audience {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response createAndAppend(
      HttpHeaders headers, UriInfo uriInfo, api.representations.json.Audience audience) {
    String className = AudienceResource.class.getName();
    String spanName = String.format("%s#createAndAppend", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      UUID audienceUUID =
          this.audienceService.createAudience(this.audienceFactory.createFrom(audience));
      URI location =
          UriBuilder.fromUri(uriInfo.getRequestUri())
              .path("/{uuid}/")
              .build(audienceUUID.toString());
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
   * @param audience {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response createAndAppend(
      HttpHeaders headers, UriInfo uriInfo, api.representations.xml.Audience audience) {
    String className = AudienceResource.class.getName();
    String spanName = String.format("%s#createAndAppend", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      UUID audienceUUID =
          this.audienceService.createAudience(this.audienceFactory.createFrom(audience));
      URI location =
          UriBuilder.fromUri(uriInfo.getRequestUri())
              .path("/{uuid}/")
              .build(audienceUUID.toString());
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
   * @param audience {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response replace(
      HttpHeaders headers, UriInfo uriInfo, api.representations.json.Audience audience) {
    String className = AudienceResource.class.getName();
    String spanName = String.format("%s#replace", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      this.audienceService.replaceAudience(this.audienceFactory.createFrom(audience));
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
   * @param audience {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response replace(
      HttpHeaders headers, UriInfo uriInfo, api.representations.xml.Audience audience) {
    String className = AudienceResource.class.getName();
    String spanName = String.format("%s#replace", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      this.audienceService.replaceAudience(this.audienceFactory.createFrom(audience));
      return Response.noContent().build();
    } finally {
      span.finish();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param uriInfo {@inheritDoc}
   * @param uuid {@inheritDoc}
   */
  @Override
  public Response delete(UriInfo uriInfo, String uuid) {
    String className = AudienceResource.class.getName();
    String spanName = String.format("%s#delete", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      this.audienceService.deleteAudience(UUID.fromString(uuid));
      return Response.noContent().build();
    } finally {
      span.finish();
    }
  }
}
