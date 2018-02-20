package api.filters;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import javax.inject.Inject;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
//import javax.ws.rs.ext.Provider;

import infrastructure.EntityTagService;
import infrastructure.ResourceMetadata;
import infrastructure.ResourceMetadataService;

//@Provider
public abstract class MetadataFilter implements ContainerResponseFilter {

	private final ResourceMetadataService resourceMetadataService;
	private final EntityTagService entityTagService;

	@Inject
	public MetadataFilter(
		ResourceMetadataService resourceMetadataService,
		EntityTagService entityTagService
	) {
		this.resourceMetadataService = resourceMetadataService;
		this.entityTagService = entityTagService;
	}

	@Override
	public abstract void filter(
		ContainerRequestContext requestContext,
		ContainerResponseContext responseContext
	);

	public boolean isPostRequest(ContainerRequestContext requestContext) {
		return requestContext.getRequest().getMethod().equalsIgnoreCase("POST");
	}

	public boolean isGetRequest(ContainerRequestContext requestContext) {
		return requestContext.getRequest().getMethod().equalsIgnoreCase("GET");
	}

	public boolean isPutRequest(ContainerRequestContext requestContext) {
		return requestContext.getRequest().getMethod().equalsIgnoreCase("PUT");
	}

	public boolean isDeleteRequest(ContainerRequestContext requestContext) {
		return requestContext.getRequest().getMethod().equalsIgnoreCase("DELETE");
	}

	public boolean isPatchRequest(ContainerRequestContext requestContext) {
		return requestContext.getRequest().getMethod().equalsIgnoreCase("PATCH");
	}

	public boolean isHeadRequest(ContainerRequestContext requestContext) {
		return requestContext.getRequest().getMethod().equalsIgnoreCase("HEAD");
	}
	
	public boolean isErrorResponse(ContainerResponseContext responseContext) {
		return responseContext.getStatus() >= 400;
	}
	
	public boolean isNotModifiedResponse(ContainerResponseContext responseContext) {
		return responseContext.getStatus() == 304;
	}

	public boolean isPreconditionFailedResponse(ContainerResponseContext responseContext) {
		return responseContext.getStatus() == 412;
	}
	
	public URI getRequestUri(ContainerRequestContext requestContext) {
		return requestContext.getUriInfo().getRequestUri();
	}
	
	public MediaType getResponseMediaType(ContainerResponseContext responseContext) {
		return responseContext.getMediaType();
	}
	
	public MediaType getRequestMediaType(ContainerRequestContext requestContext) {
		return requestContext.getMediaType();
	}

	ResourceMetadataService getResourceMetadataService() {
		return this.resourceMetadataService;
	}
	
	EntityTagService getEntityTagService() {
		return this.entityTagService;
	}

	//basically find or create.
	void get(
		ContainerRequestContext requestContext,
		ContainerResponseContext responseContext
	) {

		URI requestUri = requestContext.getUriInfo().getRequestUri();
		MediaType mediaType = responseContext.getMediaType();

		//search storage of resource metadata.
		ResourceMetadata metadata = this.find(requestUri, mediaType);

		//guard: if resource representation metadata already exists our work is done.
		if(metadata != null){
			responseContext.getHeaders().add("Last-Modified", metadata.getLastModified());
			responseContext.getHeaders().add("ETag", metadata.getEntityTag());
			return;
		}

		//generate an entity tag.
		String nodeName = UUID.randomUUID().toString();
		Long revision = 0l;
		EntityTag entityTag = this.entityTagService.generateTag(nodeName, revision);

		//set the Last-Modified header to the current time (in UTC).
		Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Date lastModified = utcCalendar.getTime();
		MediaType contentType = responseContext.getMediaType();

		//persist the resource representation metadata..
		this.resourceMetadataService.insert(
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

	/**
	 * Searches for resource representation metadata provided the
	 * request URI and media types specified in the Accept HTTP request header.
	 *
	 * @param uri	The URI of the request.
	 * @param mediaTypes	The media types specified in the Accept HTTP request header.
	 * @return	An instance of {@link ResourceMetadata} containing the resource metadata
	 *			about the resource with the provided URI and one of the media types provided.
	 *			If metadata exists for more than one representation of the same resource, the
	 *			metadata that matches first based on the order of the provided media types is
	 *			returned.
	 */
	private ResourceMetadata find(URI uri, MediaType mediaType) {
		ResourceMetadata resourceMetadata =
			this.resourceMetadataService.get(uri, mediaType);
		return resourceMetadata;
	}

	void post(
		ContainerRequestContext requestContext,
		ContainerResponseContext responseContext
	) {

		Date lastModified = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		String nodeName = UUID.randomUUID().toString();
		Long revision = 0l;
		EntityTag entityTag = this.entityTagService.generateTag(nodeName, revision);

		this.resourceMetadataService.insert(
			new ResourceMetadata(
				responseContext.getLocation(),
				requestContext.getMediaType(),
				nodeName,
				revision,
				lastModified,
				entityTag
			)
		);
	}

	void delete(
		ContainerRequestContext requestContext,
		ContainerResponseContext responseContext
	) {
		URI requestUri = requestContext.getUriInfo().getRequestUri();
		this.resourceMetadataService.removeAll(requestUri);
	}

	void put(
		ContainerRequestContext requestContext,
		ContainerResponseContext responseContext
	) {
		URI requestUri = requestContext.getUriInfo().getRequestUri();
		MediaType contentType = requestContext.getMediaType();

		ResourceMetadata resourceMetadata = 
			this.resourceMetadataService.get(
				requestUri,
				contentType
			);
		System.out.println("FOUND REPRESENTATION METADATA!");

		//update resource metadata.
		Date lastModified = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		EntityTag entityTag = null;

		if(resourceMetadata != null){
			resourceMetadata.incrementRevision();
			entityTag = this.entityTagService.generateTag(
					resourceMetadata.getNodeName(),
					resourceMetadata.getRevision()
				);

			this.resourceMetadataService.put(
				new ResourceMetadata(
					resourceMetadata.getUri(),
					resourceMetadata.getContentType(),
					resourceMetadata.getNodeName(),
					resourceMetadata.getRevision(),
					lastModified,
					entityTag
				)
			);

			System.out.println("UPDATED REPRESENTATION METADATA!");

		} else {

			String nodeName = UUID.randomUUID().toString();
			Long revision = 0l;
			entityTag = this.entityTagService.generateTag(nodeName, revision);
			
			this.resourceMetadataService.insert(
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

	void patch(
		ContainerRequestContext requestContext,
		ContainerResponseContext responseContext
	) {
		this.put(requestContext, responseContext);
	}

	void head(
		ContainerRequestContext requestContext,
		ContainerResponseContext responseContext
	) {
		this.get(requestContext, responseContext);
	}
}
