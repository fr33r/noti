package api.filters;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import infrastructure.ResourceMetadataService;
import infrastructure.ResourceMetadata;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

@Provider
public class ConditionalPutFilter implements ContainerRequestFilter {

	private ResourceMetadataService resourceMetadataService;
	private final Tracer tracer;

	@Inject
	public ConditionalPutFilter(
		ResourceMetadataService resourceMetadataService,
		Tracer tracer
	){
		this.resourceMetadataService = resourceMetadataService;
		this.tracer = tracer;
	}

	public void filter(ContainerRequestContext requestContext) throws IOException {
		Span span =
			this.tracer
				.buildSpan("ConditionalPutFilter#filter")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			Request request = requestContext.getRequest();
			UriInfo uriInfo = requestContext.getUriInfo();

			if(request.getMethod().equalsIgnoreCase("PUT")){
				MediaType contentType = requestContext.getMediaType();
				if (contentType == null) { return; }

				ResourceMetadata resourceMetadata = 
					this.resourceMetadataService.get(uriInfo.getRequestUri(), contentType);

				if(resourceMetadata != null){
					
					ResponseBuilder responseBuilder = 
						request.evaluatePreconditions(
							resourceMetadata.getLastModified(), 
							resourceMetadata.getEntityTag()
						);

					if(responseBuilder != null){
						requestContext.abortWith(responseBuilder.build());
					}
				}
			}
		} finally {
			span.finish();
		}
	}
}
