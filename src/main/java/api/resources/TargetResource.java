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
public final class TargetResource implements api.TargetResource {

  private final TargetService targetService;
  private final RepresentationFactory jsonRepresentationFactory;
  private final RepresentationFactory xmlRepresentationFactory;
  private final RepresentationFactory sirenRepresentationFactory;
  private final TargetFactory targetFactory;
  private final Tracer tracer;

  /**
   * Construct a new {@link TargetResource}.
   *
   * @param jsonRepresentationFactory The {@link api.representations.json.JSONRepresentationFactory}
   *     responsible for constructing JSON {@link api.representations.Representation} instances.
   * @param xmlRepresentationFactory The {@link api.representations.xml.XMLRepresentationFactory}
   *     responsible for constructing XML {@link api.representations.Representation} instances.
   * @param sirenRepresentationFactory The {@link
   *     api.representations.siren.SirenRepresentationFactory} responsible for constructing Siren
   *     {@link api.representations.Representation} instances.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   * @param targetService The application service that orchestrates various operations with targets.
   */
  @Inject
  public TargetResource(
      TargetService targetService,
      @Named("JSONRepresentationFactory") RepresentationFactory jsonRepresentationFactory,
      @Named("XMLRepresentationFactory") RepresentationFactory xmlRepresentationFactory,
      @Named("SirenRepresentationFactory") RepresentationFactory sirenRepresentationFactory,
      Tracer tracer) {
    this.targetService = targetService;
    this.jsonRepresentationFactory = jsonRepresentationFactory;
    this.xmlRepresentationFactory = xmlRepresentationFactory;
    this.sirenRepresentationFactory = sirenRepresentationFactory;
    this.tracer = tracer;
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
    Span span = this.tracer.buildSpan("TargetResource#get").start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      URI requestURI = uriInfo.getRequestUri();
      Locale language = null;
      application.Target target = this.targetService.getTarget(UUID.fromString(uuid));

      // instead of doing it this way, i think it would be better to have:
      // RepresentationFactory representationFactory = ...conditional logic to figure out which one.
      // representationFactory.createTargetRepresentation(uriInfo, target);

      // TODO - change the following conditional blocks to honer q-value hierarchy.
      Representation representation = null;
      if (headers
          .getAcceptableMediaTypes()
          .contains(new MediaType("application", "vnd.siren+json"))) {
        representation =
            this.sirenRepresentationFactory.createTargetRepresentation(
                requestURI, language, target);
      } else if (headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_XML_TYPE)) {
        representation =
            this.xmlRepresentationFactory.createTargetRepresentation(requestURI, language, target);
      } else if (headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_JSON_TYPE)) {
        representation =
            this.jsonRepresentationFactory.createTargetRepresentation(requestURI, language, target);
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
   * @param target {@inheritDoc}
   * @return {@inheritDoc}
   */
  public Response createAndAppend(HttpHeaders headers, UriInfo uriInfo, Target target) {
    Span span = this.tracer.buildSpan("TargetResource#createAndAppend").start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
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
    Span span = this.tracer.buildSpan("TargetResource#replace").start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
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
    Span span = this.tracer.buildSpan("TargetResource#replace").start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      this.targetService.deleteTarget(UUID.fromString(uuid));
      return Response.noContent().build();
    } finally {
      span.finish();
    }
  }
}
