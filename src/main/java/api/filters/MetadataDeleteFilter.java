package api.filters;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.ext.Provider;

import infrastructure.EntityTagService;
import infrastructure.ResourceMetadataService;

@Provider
public class MetadataDeleteFilter extends MetadataFilter {

	@Inject
	public MetadataDeleteFilter(
		ResourceMetadataService resourceMetadataService,
		EntityTagService entityTagService
	) {
		super(resourceMetadataService, entityTagService);
	}
	
	@Override
	public void filter(
		ContainerRequestContext requestContext,
		ContainerResponseContext responseContext
	) {
		//guard: don't proceed if response is an error or is a response to conditional request.
		if(this.isErrorResponse(responseContext) || this.isNotModifiedResponse(responseContext)) {
			return;
		}

		//guard: don't proceed if the request is not an HTTP GET request.
		if(!this.isDeleteRequest(requestContext)) return;

		URI requestUri = this.getRequestUri(requestContext);
		this.getResourceMetadataService().removeAll(requestUri);
	}

}
