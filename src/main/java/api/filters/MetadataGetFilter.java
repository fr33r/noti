package api.filters;

import infrastructure.RepresentationMetadata;
import infrastructure.RepresentationMetadataService;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.net.URI;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.inject.Inject;
import javax.ws.rs.core.EntityTag;
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

      byte[] representationBytes = requestContext.getEntityBytes();
      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] hashedBytes = digest.digest(representationBytes);
      String hashedBytesBase64 = Base64.getEncoder().encodeToString(hashedBytes);

      URI location = requestContext.getRequestUri();
      MediaType mediaType = responseContext.getMediaType();
      Locale language = responseContext.getLanguage();
      List<String> encodings = responseContext.getEncodings();
      EntityTag entityTag = new EntityTag(hashedBytesBase64);
      Date lastModified = calendar.getTime();

      // persist the resource representation metadata.
      this.representationMetadataService.put(
          new RepresentationMetadata(
              location, mediaType, language, String.join(",", encodings), lastModified, entityTag));

      // set the Last-Modified and ETag response headers.
      responseContext.setLastModified(lastModified);
      responseContext.setEntityTag(entityTag);
    } catch (Exception x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }
}
