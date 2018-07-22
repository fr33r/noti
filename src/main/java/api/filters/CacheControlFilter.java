package api.filters;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import javax.inject.Inject;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

@Provider
public class CacheControlFilter extends ResponseFilter {

  private final Tracer tracer;

  @Inject
  public CacheControlFilter(Tracer tracer) {
    this.tracer = tracer;
  }

  public void filter(RequestContext requestContext, ResponseContext responseContext) {

    Span span =
        this.tracer
            .buildSpan("CacheControlFilter#filter")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      // do i really need this? there has got to be a way
      // that response filters do not run when an exception occurs.
      if (responseContext.getStatus() >= 400) {
        return;
      }

      Request request = requestContext.getRequest();

      if (request.getMethod().equalsIgnoreCase("GET")) {

        UriInfo uriInfo = requestContext.getUriInfo();

        // for now, not providing caching abilities of search results.
        if (uriInfo.getQueryParameters().isEmpty()) {

          CacheControl cacheControl = new CacheControl();
          // cacheControl.setPrivate(true);
          cacheControl.setMaxAge(300);

          responseContext.getHeaders().add("Cache-Control", cacheControl);
        }
      }
    } finally {
      span.finish();
    }
  }
}
