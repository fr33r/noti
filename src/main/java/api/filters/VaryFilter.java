package api.filters;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Request;
import javax.ws.rs.ext.Provider;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

@Provider
public class VaryFilter implements ContainerResponseFilter {

	private final Tracer tracer;

	@Inject
	public VaryFilter(Tracer tracer) {
		this.tracer = tracer;
	}

	public void filter(
		ContainerRequestContext requestContext,
		ContainerResponseContext responseContext
	) throws IOException {
		Span span =
			this.tracer
				.buildSpan("VaryFilter#filter")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			if(responseContext.getStatus() >= 400) { return; }

			Request request = requestContext.getRequest();

			if(request.getMethod().equalsIgnoreCase("GET")){
				responseContext.getHeaders().add("Vary", "Accept");
			}
		} finally {
			span.finish();
		}
	}
}
