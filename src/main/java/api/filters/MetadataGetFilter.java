package api.filters;

import api.representations.Representation;
import infrastructure.RepresentationMetadata;
import infrastructure.RepresentationMetadataService;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.inject.Inject;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
public class MetadataGetFilter extends ResponseFilter {

  private final Calendar calendar;
  private final Tracer tracer;
  private final RepresentationMetadataService representationMetadataService;

  @Inject
  public MetadataGetFilter(
      RepresentationMetadataService representationMetadataService, Tracer tracer) {
    this.representationMetadataService = representationMetadataService;
    this.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    this.tracer = tracer;
  }

  @Override
  public void filter(RequestContext requestContext, ResponseContext responseContext) {
    Span span =
        this.tracer
            .buildSpan("MetadataGetFilter#filter")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      // guard: don't proceed if response is an error or is a response to conditional request.
      if (responseContext.statusIsServerError()
          || responseContext.statusIsClientError()
          || responseContext.statusIs(304)) {
        return;
      }

      // guard: don't proceed if the request is not an HTTP GET or HEAD request.
      if (!requestContext.methodIs("GET") && !requestContext.methodIs("HEAD")) return;

      URI location = requestContext.getRequestUri();
      MediaType mediaType = responseContext.getMediaType();
      Locale language = responseContext.getLanguage();
      String encodings = responseContext.getHeaderString(HttpHeaders.CONTENT_ENCODING);

      // thoughts
      // - perhaps abstract storage mediums behind infrastructure service interface.
      //   - could even abstract hierarchy of storage mediums.
      // - don't generate Last-Modified on the fly; only pull it from storage.
      //   - if we did it on the fly, it would be the current timestamp each response :(.
      // - should create factory or some other mechanism to abstract creation of
      // RepresentationMetadata.
      // - ensure that we do not place Last-Modified value unless cache validation succeeds.
      // - consider breaking this filter up more logically by utilizing middleware prioritization.
      if (!(responseContext.getEntity() instanceof Representation)) {
        return;
      }

      Representation representation = (Representation) responseContext.getEntity();
      EntityTag entityTag = representation.getEntityTag();
      Date lastModified = calendar.getTime();

      // persist the resource representation metadata.
      this.representationMetadataService.put(
          new RepresentationMetadata(
              location, mediaType, language, encodings, lastModified, entityTag));

      // set the Last-Modified and ETag response headers.
      responseContext.setLastModified(lastModified);
      responseContext.setEntityTag(entityTag);
    } finally {
      span.finish();
    }
  }
}
