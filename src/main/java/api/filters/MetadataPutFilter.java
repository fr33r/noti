package api.filters;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

@Provider
public class MetadataPutFilter extends MetadataFilter {

	private final Tracer tracer;

	@Inject
	public MetadataPutFilter(
		ResourceMetadataService resourceMetadataService,
		EntityTagService entityTagService,
		Tracer tracer
	) {
		super(resourceMetadataService, entityTagService);

		this.tracer = tracer;
	}

	//TODO: Figure out on which requests Last-Modified and ETag should be returned.
	//TODO: Think harder about the value of storing metadata before HTTP GET request.
	@Override
	public void filter(
		ContainerRequestContext requestContext,
		ContainerResponseContext responseContext
	) {
		Span span =
			this.tracer
				.buildSpan("MetadataPutFilter#filter")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			//guard: don't proceed if response is an error or is a response to conditional request.
			if(
				this.isErrorResponse(responseContext) ||
				this.isNotModifiedResponse(responseContext) ||
				this.isPreconditionFailedResponse(responseContext)
			) {
				return;
			}

			//guard: don't proceed if the request is not an HTTP PUT request.
			if(!this.isPutRequest(requestContext)) return;
			
			URI requestUri = this.getRequestUri(requestContext);
			MediaType contentType = this.getRequestMediaType(requestContext);

			ResourceMetadata resourceMetadata = 
				this.getResourceMetadataService().get(
					requestUri,
					contentType
				);

			//update resource metadata.
			Date lastModified = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
			EntityTag entityTag = null;

			List<ResourceMetadata> representationMetadata =
				this.getResourceMetadataService().getAll(requestUri);

			//if no representation exists, persist new metadata,
			if(representationMetadata.size() < 1) {
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

				return;
			}

			//update all representation metadata.
			for(ResourceMetadata metadata : representationMetadata) {
				metadata.incrementRevision();
				entityTag = this.getEntityTagService().generateTag(
						metadata.getNodeName(),
						metadata.getRevision()
					);

				this.getResourceMetadataService().put(
					new ResourceMetadata(
						metadata.getUri(),
						metadata.getContentType(),
						metadata.getNodeName(),
						metadata.getRevision(),
						lastModified,
						entityTag
					)
				);
			}
		} finally {
			span.finish();
		}
	}
}
