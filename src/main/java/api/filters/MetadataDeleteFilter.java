package api.filters;

import infrastructure.RepresentationMetadataService;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.net.URI;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

@Provider
public class MetadataDeleteFilter extends ResponseFilter {

  private final RepresentationMetadataService representationMetadataService;
  private final Tracer tracer;

  @Inject
  public MetadataDeleteFilter(
      RepresentationMetadataService representationMetadataService, Tracer tracer) {
    this.representationMetadataService = representationMetadataService;
    this.tracer = tracer;
  }

  @Override
  public void filter(RequestContext requestContext, ResponseContext responseContext) {
    Span span =
        this.tracer
            .buildSpan("MetadataDeleteFilter#filter")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      // guard: don't proceed if response is an error or is a response to conditional request.
      if (responseContext.statusIsServerError()
          || responseContext.statusIsClientError()
          || responseContext.statusIs(304)) {
        return;
      }

      // guard: don't proceed if the request is not an HTTP GET request.
      if (!requestContext.methodIs("DELETE")) return;

      URI requestUri = requestContext.getRequestUri();
      this.representationMetadataService.removeAll(requestUri);
    } finally {
      span.finish();
    }
  }
}
