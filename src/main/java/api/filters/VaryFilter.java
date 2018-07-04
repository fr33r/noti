package api.filters;


import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import javax.inject.Inject;
import javax.ws.rs.core.Request;
import javax.ws.rs.ext.Provider;

@Provider
public class VaryFilter extends ResponseFilter {

  private final Tracer tracer;

  @Inject
  public VaryFilter(Tracer tracer) {
    this.tracer = tracer;
  }

  public void filter(RequestContext requestContext, ResponseContext responseContext) {
    Span span =
        this.tracer.buildSpan("VaryFilter#filter").asChildOf(this.tracer.activeSpan()).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      if (responseContext.getStatus() >= 400) {
        return;
      }

      Request request = requestContext.getRequest();

      if (request.getMethod().equalsIgnoreCase("GET")) {
        responseContext.getHeaders().add("Vary", "Accept");
      }
    } finally {
      span.finish();
    }
  }
}
