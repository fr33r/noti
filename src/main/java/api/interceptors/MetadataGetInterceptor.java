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
import javax.ws.rs.Priorities;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;

@Priority(Priorities.HEADER_DECORATOR)
public final class MetadataGetInterceptor extends WriterInterceptor {

  private final RepresentationMetadataService representationMetadataService;
  private final Logger logger;
  private Tracer tracer;
  private Calendar calendar;

  @Inject
  public MetadataGetInterceptor(
      RepresentationMetadataService representationMetadataService, Tracer tracer, Logger logger) {
    this.representationMetadataService = representationMetadataService;
    this.tracer = tracer;
    this.logger = logger;
    this.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
  }

  @Override
  public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext) {

    Span span =
        this.tracer
            .buildSpan("MetadataGetInterceptor#aroundWriteTo")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      boolean statusIsServerError = writerInterceptorContext.getResponse().getStatus() / 100 == 5;
      boolean statusIsClientError = writerInterceptorContext.getResponse().getStatus() / 100 == 4;
      boolean statusIsNotModified = writerInterceptorContext.getResponse().getStatus() == 304;

      // guard: don't proceed if response is an error or is a response to conditional request.
      if (statusIsServerError || statusIsClientError || statusIsNotModified) {
        writerInterceptorContext.proceed();
        return;
      }

      // guard: don't proceed if request is not an HTTP GET or HEAD request.
      if (!writerInterceptorContext.getRequest().getMethod().equalsIgnoreCase("GET")
          && !writerInterceptorContext.getRequest().getMethod().equalsIgnoreCase("HEAD")) {
        writerInterceptorContext.proceed();
        return;
      }

      byte[] representationBytes = writerInterceptorContext.getEntityBytes();
      this.logger.debug(
          String.format("Representation is %d bytes long.", representationBytes.length));
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

      this.logger.debug(String.format("Content Location: %s", location));
      this.logger.debug(String.format("Content Type: %s", mediaType));
      this.logger.debug(String.format("Content Language: %s", language));
      this.logger.debug(String.format("Content Encoding: %s", encodings));

      // persist the resource representation metadata.
      this.representationMetadataService.put(
          new RepresentationMetadata(
              location, mediaType, language, encodings, lastModified, entityTag));

      // set the Last-Modified and ETag response headers.
      writerInterceptorContext.getHeaders().putSingle(HttpHeaders.LAST_MODIFIED, lastModified);
      writerInterceptorContext.getHeaders().putSingle(HttpHeaders.ETAG, entityTag);
    } catch (Exception x) {
      this.logger.error("Encountered an issue when persisting representation metadata.", x);
      return;
    } finally {
      span.finish();
    }
  }
}
