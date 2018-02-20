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
public class MetadataPutFilter extends MetadataFilter {

	@Inject
	public MetadataPutFilter(
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

		//guard: don't proceed if the request is not an HTTP PUT request.
		if(!this.isPutRequest(requestContext)) return;
		
		URI requestUri = this.getRequestUri(requestContext);
		MediaType contentType = this.getRequestMediaType(requestContext);

		//should actually update all resource representations metadata!
		//updates are simply tracked by incrementing a revision; a PUT
		//request on a resource regardless of which content type is used
		//in the request means that all representations should bump their revisions.
		//it is a little over-cautious; i suppose its possible that one representation of
		//a resource would change while another would not, but that would insinuate that
		//data exposed in one representation is not exposed in another. since the client
		//is acting on that data, that would appear to be more a gap in functionality 
		//than a desired feature.
		//TODO: add getAll() interaction with ResourceMetadataService to retrieve
		//all representation metadata for a single resource (by URI) and
		//increment the revision for each.
		ResourceMetadata resourceMetadata = 
			this.getResourceMetadataService().get(
				requestUri,
				contentType
			);

		//update resource metadata.
		Date lastModified = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		EntityTag entityTag = null;

		if(resourceMetadata != null){
			resourceMetadata.incrementRevision();
			entityTag = this.getEntityTagService().generateTag(
					resourceMetadata.getNodeName(),
					resourceMetadata.getRevision()
				);

			this.getResourceMetadataService().put(
				new ResourceMetadata(
					resourceMetadata.getUri(),
					resourceMetadata.getContentType(),
					resourceMetadata.getNodeName(),
					resourceMetadata.getRevision(),
					lastModified,
					entityTag
				)
			);

		//do we even need to do this? perhaps leave it up to MetadataGetFilter?
		//the whole point of having metadata in place is so that it can be returned 
		//in GET requests for caching purposes. although just now thinking about it,
		//if we don't capture metadata prior to the first GET, all PUT requests prior to 
		//the first GET requests will not be able to leverage conditional PUT.
		//that begs the question, for which responses do we need to return ETag and Last-Modified?
		//if it is only GET, then the client will need to issue a GET first before being able to issue a 
		//conditional PUT anyway.
		} else { 

			String nodeName = UUID.randomUUID().toString();
			Long revision = 0l;
			entityTag = this.getEntityTagService().generateTag(nodeName, revision);
			
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
		}

		responseContext.getHeaders().add("Last-Modified", lastModified);
		responseContext.getHeaders().add("ETag", entityTag);
	}
}
