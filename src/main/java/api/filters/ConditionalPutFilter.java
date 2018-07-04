package api.filters;

import infrastructure.RepresentationMetadata;
import infrastructure.RepresentationMetadataService;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.io.IOException;
import java.util.Locale;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

@Provider
public class ConditionalPutFilter extends RequestFilter {

  private RepresentationMetadataService representationMetadataService;
  private final Tracer tracer;

  @Inject
  public ConditionalPutFilter(
      RepresentationMetadataService representationMetadataService, Tracer tracer) {
    this.representationMetadataService = representationMetadataService;
    this.tracer = tracer;
  }

  public void filter(RequestContext requestContext) throws IOException {
    Span span =
        this.tracer
            .buildSpan("ConditionalPutFilter#filter")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Request request = requestContext.getRequest();
      UriInfo uriInfo = requestContext.getUriInfo();

      if (request.getMethod().equalsIgnoreCase("PUT")) {
        MediaType contentType = requestContext.getMediaType();

        if (contentType == null) {
          return;
        }
        Locale language = requestContext.getLanguage();
        // PUT LINK HERE DESCRIBING HOW MULTIPLE ENCODINGS CAN BE PRESENT FOR THE REPRESENTATION.
        String encodings = requestContext.getHeaderString(HttpHeaders.CONTENT_ENCODING);

        RepresentationMetadata representationMetadata =
            this.representationMetadataService.get(
                uriInfo.getRequestUri(), language, encodings, contentType);

        if (representationMetadata != null) {

          ResponseBuilder responseBuilder =
              request.evaluatePreconditions(
                  representationMetadata.getLastModified(), representationMetadata.getEntityTag());

          if (responseBuilder != null) {
            requestContext.abortWith(responseBuilder.build());
          }
        }
      }
    } finally {
      span.finish();
    }
  }
}
