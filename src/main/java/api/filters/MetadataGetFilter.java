package api.filters;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import infrastructure.EntityTagService;
import infrastructure.ResourceMetadata;
import infrastructure.ResourceMetadataService;

@Provider
public class MetadataGetFilter extends MetadataFilter {

	@Inject
	public MetadataGetFilter(
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
		if(!this.isGetRequest(requestContext) && !this.isHeadRequest(requestContext)) return;

		URI requestUri = this.getRequestUri(requestContext);
		MediaType mediaType = this.getResponseMediaType(responseContext);

		ResourceMetadata metadata =
			this.getResourceMetadataService().get(requestUri, mediaType);

		//guard: if resource representation metadata already exists our work is done.
		if(metadata != null){
			responseContext.getHeaders().add("Last-Modified", metadata.getLastModified());
			responseContext.getHeaders().add("ETag", metadata.getEntityTag());
			return;
		}

		//perhaps create a factory to make ResourceMetadata instances.
		//client code should not have to worry about how to construct
		//these instances, especially when there is logic like creating UUIDs,
		//incrementing revisions, etc.

		//generate an entity tag.
		String nodeName = UUID.randomUUID().toString();
		Long revision = 0l;
		EntityTag entityTag = this.getEntityTagService().generateTag(nodeName, revision);

		//set the Last-Modified header to the current time (in UTC).
		Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Date lastModified = utcCalendar.getTime();
		MediaType contentType = responseContext.getMediaType();

		//persist the resource representation metadata.
		this.getResourceMetadataService().insert(
			new ResourceMetadata(
				requestUri,
				contentType,
				nodeName,
				revision,
				lastModified,
				entityTag
			)
		);

		responseContext.getHeaders().add("Last-Modified", lastModified);
		responseContext.getHeaders().add("ETag", entityTag);
	}
}
