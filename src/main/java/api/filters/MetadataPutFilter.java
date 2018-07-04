package api.filters;

import infrastructure.RepresentationMetadata;
import infrastructure.RepresentationMetadataService;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.inject.Inject;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
public class MetadataPutFilter extends ResponseFilter {

  private final Calendar calendar;
  private final Tracer tracer;
  private final RepresentationMetadataService representationMetadataService;

  @Inject
  public MetadataPutFilter(
      RepresentationMetadataService representationMetadataService, Tracer tracer) {
    this.representationMetadataService = representationMetadataService;
    this.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    this.tracer = tracer;
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

      byte[] representationBytes = this.getRequestBytes(requestContext);
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
      String encoding = requestContext.getEncoding();

      this.representationMetadataService.put(
          new RepresentationMetadata(
              requestUri, contentType, language, encoding, lastModified, entityTag));
    } catch (Exception x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }

  private byte[] getRequestBytes(RequestContext requestContext) throws Exception {
    // convert the input stream containing the representation to a byte array.
    InputStream is = requestContext.getEntityStream();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final int bufferSize = 2048;
    final int done = -1;
    byte[] buffer = new byte[bufferSize];
    int numBytesRead;

    while ((numBytesRead = is.read(buffer, 0, buffer.length)) != done) {
      baos.write(buffer, 0, numBytesRead);
    }
    baos.flush();
    byte[] representationBytes = baos.toByteArray();
    return representationBytes;
  }
}
