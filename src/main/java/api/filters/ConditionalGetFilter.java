package api.filters;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;

import infrastructure.ResourceMetadataService;
import infrastructure.ResourceMetadata;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

@Provider
public class ConditionalGetFilter implements ContainerRequestFilter {

	private final ResourceMetadataService resourceMetadataService;
	private final Tracer tracer;

	@Inject
	public ConditionalGetFilter(
		ResourceMetadataService resourceMetadataService,
		Tracer tracer
	) {
		this.resourceMetadataService = resourceMetadataService;
		this.tracer = tracer;
	}

	public void filter(ContainerRequestContext requestContext) throws IOException {
		Span span =
			this.tracer
				.buildSpan("ConditionalGetFilter#filter")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			Request request = requestContext.getRequest();

			if(request.getMethod().equalsIgnoreCase("GET")){

				List<MediaType> acceptHeader = requestContext.getAcceptableMediaTypes();
				if (acceptHeader.size() < 1) { return; }

				// search for a match for all values of accept; use first match.
				ResourceMetadata resourceMetadata = null;
				for(MediaType mediaType : acceptHeader) {
					resourceMetadata = 
						this.resourceMetadataService.get(
							requestContext.getUriInfo().getRequestUri(),
							mediaType
						);
					if (resourceMetadata != null) {
						break;
					}
				}

				if(resourceMetadata != null){
					ResponseBuilder responseBuilder = 
						request.evaluatePreconditions(
							resourceMetadata.getLastModified(),
							resourceMetadata.getEntityTag()
						);

					if(responseBuilder != null){
						//https://tools.ietf.org/html/rfc7232#section-4.1
						responseBuilder.header("Content-Type", resourceMetadata.getContentType().toString());
						responseBuilder.tag(resourceMetadata.getEntityTag());
						responseBuilder.lastModified(resourceMetadata.getLastModified());
						Response response = responseBuilder.build();
						
						requestContext.abortWith(response);
					}
				}	
			}
		} finally {
			span.finish();
		}
	}
}
