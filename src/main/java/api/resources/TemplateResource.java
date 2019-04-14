package api.resources;

import api.representations.Representation;
import api.representations.RepresentationFactory;
import application.TemplateFactory;
import application.TemplateService;
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
public final class TemplateResource extends Resource implements api.TemplateResource {

  private final TemplateService templateService;
  private final TemplateFactory templateFactory;

  /**
   * Construct a new {@link TemplateResource}.
   *
   * @param representationIndustry The collection of representation factories used to construct
   *     representations.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   * @param templateService The application service that orchestrates various operations with
   *     templates.
   */
  @Inject
  public TemplateResource(
      TemplateService templateService,
      Map<MediaType, RepresentationFactory> representationIndustry,
      Tracer tracer) {
    super(representationIndustry, tracer);
    this.templateService = templateService;
    this.templateFactory = new TemplateFactory();
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
    String className = TemplateResource.class.getName();
    String spanName = String.format("%s#get", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      URI location = uriInfo.getRequestUri();
      Locale language = null;
      application.Template template = this.templateService.getTemplate(UUID.fromString(uuid));

      RepresentationFactory representationFactory = this.getRepresentationFactory(headers);
      Representation representation =
          representationFactory.createTemplateRepresentation(location, language, template);
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
   * @param template {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response createAndAppend(
      HttpHeaders headers, UriInfo uriInfo, api.representations.json.Template template) {
    String className = TemplateResource.class.getName();
    String spanName = String.format("%s#createAndAppend", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      UUID uuid = this.templateService.createTemplate(this.templateFactory.createFrom(template));
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
   * @param template {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response createAndAppend(
      HttpHeaders headers, UriInfo uriInfo, api.representations.xml.Template template) {
    String className = TemplateResource.class.getName();
    String spanName = String.format("%s#createAndAppend", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      UUID uuid = this.templateService.createTemplate(this.templateFactory.createFrom(template));
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
   * @param template {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response replace(
      HttpHeaders headers, UriInfo uriInfo, api.representations.json.Template template) {
    String className = TemplateResource.class.getName();
    String spanName = String.format("%s#replace", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      this.templateService.replaceTemplate(this.templateFactory.createFrom(template));
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
   * @param template {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Response replace(
      HttpHeaders headers, UriInfo uriInfo, api.representations.xml.Template template) {
    String className = TemplateResource.class.getName();
    String spanName = String.format("%s#replace", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      this.templateService.replaceTemplate(this.templateFactory.createFrom(template));
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
    String className = TemplateResource.class.getName();
    String spanName = String.format("%s#delete", className);
    Span span = this.getTracer().buildSpan(spanName).start();
    try (Scope scope = this.getTracer().scopeManager().activate(span, false)) {
      this.templateService.deleteTemplate(UUID.fromString(uuid));
      return Response.noContent().build();
    } finally {
      span.finish();
    }
  }
}
