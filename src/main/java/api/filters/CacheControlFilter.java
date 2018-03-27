package api.filters;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

@Provider
public class CacheControlFilter implements ContainerResponseFilter {

	private final Tracer tracer;

	public CacheControlFilter(Tracer tracer) {
		this.tracer = tracer;
	}

	public void filter(
		ContainerRequestContext requestContext, 
		ContainerResponseContext responseContext
	) throws IOException {
	
		Span span =
			this.tracer
				.buildSpan("CacheControlFilter#filter")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			//do i really need this? there has got to be a way
			//that response filters do not run when an exception occurs.
			if(responseContext.getStatus() >= 400){ return; }
			
			Request request = requestContext.getRequest();
			
			if(request.getMethod().equalsIgnoreCase("GET")){
				
				UriInfo uriInfo = requestContext.getUriInfo();
				
				//for now, not providing caching abilities of search results.
				if(uriInfo.getQueryParameters().isEmpty()){
					
					CacheControl cacheControl = new CacheControl();
					//cacheControl.setPrivate(true);
					cacheControl.setMaxAge(300);
					
					responseContext.getHeaders().add("Cache-Control", cacheControl);
				}
			}
		} finally {
			span.finish();
		}
	}
}
