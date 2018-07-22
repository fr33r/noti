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
import org.slf4j.Logger;

@Provider
public class MetadataPutFilter extends ResponseFilter {

  private final Calendar calendar;
  private final Tracer tracer;
  private final RepresentationMetadataService representationMetadataService;
  private final Logger logger;

  @Inject
  public MetadataPutFilter(
      RepresentationMetadataService representationMetadataService, Tracer tracer, Logger logger) {
    this.representationMetadataService = representationMetadataService;
    this.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    this.tracer = tracer;
    this.logger = logger;
  }

  @Override
  public void filter(RequestContext requestContext, ResponseContext responseContext) {
    Span span =
        this.tracer
            .buildSpan("MetadataPutFilter#filter")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      // guard: don't proceed if response is an error or is a response to conditional request.
      if (responseContext.statusIsServerError()
          || responseContext.statusIsClientError()
          || responseContext.statusIs(412)) {
        return;
      }

      // guard: don't proceed if the request is not an HTTP PUT request.
      if (!requestContext.methodIs("PUT")) return;

      byte[] representationBytes = requestContext.getEntityBytes();
      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] hashedBytes = digest.digest(representationBytes);
      String hashedBytesBase64 = Base64.getEncoder().encodeToString(hashedBytes);

      // instead of doing this, perhaps i could create another filter
      // that has highest priority and sets all of this metadata on
      // the Representation instance? what do i gain? better ETag generation?
      URI requestUri = requestContext.getRequestUri();
      MediaType contentType = requestContext.getMediaType();
      Date lastModified = calendar.getTime();
      EntityTag entityTag = new EntityTag(hashedBytesBase64);
      Locale language = requestContext.getLanguage();
      List<String> encodings = requestContext.getEncodings();

      this.representationMetadataService.put(
          new RepresentationMetadata(
              requestUri,
              contentType,
              language,
              String.join(",", encodings),
              lastModified,
              entityTag));
      this.logger.info("Representation metadata persisted.");
    } catch (Exception x) {
      this.logger.error("Encountered an issue when persisting representation metadata.", x);
      return;
    } finally {
      span.finish();
    }
  }
}
