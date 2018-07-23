package api.interceptors;

import api.interceptors.context.WriterInterceptorContext;
import infrastructure.RepresentationMetadata;
import infrastructure.RepresentationMetadataService;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.net.URI;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;

@Priority(Priorities.HEADER_DECORATOR)
public final class MetadataPutInterceptor extends WriterInterceptor {

  private final RepresentationMetadataService representationMetadataService;
  private final Logger logger;
  private final Tracer tracer;
  private Calendar calendar;

  @Inject
  public MetadataPutInterceptor(
      RepresentationMetadataService representationMetadataService,
      Tracer tracer,
      @Named("api.interceptors.MetadataPutInterceptor") Logger logger) {
    this.representationMetadataService = representationMetadataService;
    this.tracer = tracer;
    this.logger = logger;
    this.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
  }

  @Override
  public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext) {

    Span span =
        this.tracer
            .buildSpan("MetadataPutInterceptor#aroundWriteTo")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      boolean statusIsServerError = writerInterceptorContext.getResponse().getStatus() / 100 == 5;
      boolean statusIsClientError = writerInterceptorContext.getResponse().getStatus() / 100 == 4;
      boolean statusIsPreconditionFailed =
          writerInterceptorContext.getResponse().getStatus() == 412;

      // guard: don't proceed if response is an error or is a response to conditional request.
      if (statusIsServerError || statusIsClientError || statusIsPreconditionFailed) {
        writerInterceptorContext.proceed();
        return;
      }

      // guard: don't proceed if request is not an HTTP PUT request.
      if (!writerInterceptorContext.getRequest().getMethod().equalsIgnoreCase("PUT")) {
        writerInterceptorContext.proceed();
        return;
      }

      byte[] representationBytes = writerInterceptorContext.getEntityBytes();
      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] hashedBytes = digest.digest(representationBytes);
      String hashedBytesBase64 = Base64.getEncoder().encodeToString(hashedBytes);

      String requestUri = writerInterceptorContext.getRequest().getRequestURI();
      URI location = new URI(requestUri);
      MediaType mediaType = writerInterceptorContext.getMediaType();
      List<Object> contentLanguage =
          writerInterceptorContext.getHeaders().get(HttpHeaders.CONTENT_LANGUAGE);
      Locale language =
          contentLanguage == null || contentLanguage.size() == 0
              ? null
              : new Locale((String) contentLanguage.get(0));
      List<Object> contentEncoding =
          writerInterceptorContext.getHeaders().get(HttpHeaders.CONTENT_ENCODING);
      List<String> contentEncodingStrings = new ArrayList<>();
      if (contentEncoding != null) {
        for (Object encoding : contentEncoding) {
          contentEncodingStrings.add((String) encoding);
        }
      }
      String encodings =
          contentEncodingStrings == null || contentEncodingStrings.size() == 0
              ? null
              : String.join(",", contentEncodingStrings);
      EntityTag entityTag = new EntityTag(hashedBytesBase64);
      Date lastModified = calendar.getTime();

      this.logger.debug("Representation is {} bytes long.", representationBytes.length);

      // persist the resource representation metadata.
      this.representationMetadataService.put(
          new RepresentationMetadata(
              location, mediaType, language, encodings, lastModified, entityTag));

      this.logger.info("Persisted representation metadata.");
      this.logger.debug("Content Location: {}", location);
      this.logger.debug("Content Type: {}", mediaType);
      this.logger.debug("Content Language: {}", language);
      this.logger.debug("Content Encoding: {}", encodings);

    } catch (Exception x) {
      this.logger.error("Encountered an issue when persisting representation metadata.", x);
      return;
    } finally {
      span.finish();
    }
  }
}
