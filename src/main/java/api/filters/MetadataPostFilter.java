package api.filters;

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

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

@Provider
public class MetadataPostFilter extends MetadataFilter {

	private final Tracer tracer;

	@Inject
	public MetadataPostFilter(
		ResourceMetadataService resourceMetadataService,
		EntityTagService entityTagService,
		Tracer tracer
	) {
		super(resourceMetadataService, entityTagService);

		this.tracer = tracer;
	}

	@Override
	public void filter(
		ContainerRequestContext requestContext,
		ContainerResponseContext responseContext
	) {
		Span span =
			this.tracer
				.buildSpan("MetadataPostFilter#filter")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			//guard: don't proceed if response is an error or is a response to conditional request.
			if(this.isErrorResponse(responseContext) || this.isNotModifiedResponse(responseContext)) {
				return;
			}

			//guard: don't proceed if the request is not an HTTP POST request.
			if(!this.isPostRequest(requestContext)) return;

			//perhaps create a factory to make ResourceMetadata instances.
			//client code should not have to worry about how to construct
			//these instances, especially when there is logic like creating UUIDs,
			//incrementing revisions, etc.
			Date lastModified = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
			String nodeName = UUID.randomUUID().toString();
			Long revision = 0l;
			EntityTag entityTag = this.getEntityTagService().generateTag(nodeName, revision);

			//is this even right? should we even do anything for an HTTP POST request in
			//regards to generating representation metadata?
			//the reason i ask is we don't have any idea what representation will be requested
			//in the future; whether it be JSON, XML, Siren, etc.
			//this logic arbitrarily creates an entry for the media type used in the request's
			//Content-Type header; although its likely that a request's content type
			//is the desired content type in the response, this logic makes a bit of an assumption there.
			//we could just leave it up to the MetadataGetFilter to create metadata
			//for the first time, as that will be a for sure way of creating metadata for a specific
			//representation that has been requested by a client.
			MediaType contentType = this.getRequestMediaType(requestContext);

			this.getResourceMetadataService().insert(
				new ResourceMetadata(
					responseContext.getLocation(),
					contentType,
					nodeName,
					revision,
					lastModified,
					entityTag
				)
			);
		} finally {
			span.finish();
		}
	}
}
