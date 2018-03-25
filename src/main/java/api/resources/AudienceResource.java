package api.resources;

import api.representations.Audience;
import api.representations.Representation;
import api.representations.RepresentationFactory;
import application.AudienceService;

import java.net.URI;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

public class AudienceResource implements api.AudienceResource {

	private final AudienceService audienceService;
	private final RepresentationFactory jsonRepresentationFactory;
	private final RepresentationFactory xmlRepresentationFactory;
	private final RepresentationFactory sirenRepresentationFactory;
	private final Tracer tracer;

	@Inject
	public AudienceResource(
		AudienceService audienceService,
		@Named("JSONRepresentationFactory") RepresentationFactory jsonRepresentationFactory,
		@Named("XMLRepresentationFactory") RepresentationFactory xmlRepresentationFactory,
		@Named("SirenRepresentationFactory") RepresentationFactory sirenRepresentationFactory,
		Tracer tracer
	) {
		this.audienceService = audienceService;
		this.jsonRepresentationFactory = jsonRepresentationFactory;
		this.xmlRepresentationFactory = xmlRepresentationFactory;
		this.sirenRepresentationFactory = sirenRepresentationFactory;
		this.tracer = tracer;
	}

	@Override
	public Response get(HttpHeaders headers, UriInfo uriInfo, String uuid) {
		Span span = this.tracer.buildSpan("AudienceResource#get").start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			Audience audience = this.audienceService.getAudience(UUID.fromString(uuid));
		
			Representation representation = null;
			if(headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_JSON_TYPE)) {
				representation = this.jsonRepresentationFactory.createAudienceRepresentation(uriInfo, audience);
			} else if (headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_XML_TYPE)) {
				representation = this.xmlRepresentationFactory.createAudienceRepresentation(uriInfo, audience);
			} else {
				representation = this.sirenRepresentationFactory.createAudienceRepresentation(uriInfo, audience);
			}
			return Response.ok(representation).build();
		} finally {
			span.finish();
		}
	}

	@Override
	public Response createAndAppend(HttpHeaders headers, UriInfo uriInfo, Audience audience) {
		Span span = this.tracer.buildSpan("AudienceResource#createAndAppend").start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			UUID audienceUUID = this.audienceService.createAudience(audience);
			URI location =
				UriBuilder
					.fromUri(uriInfo.getRequestUri())
					.path("/{uuid}/")
					.build(audienceUUID.toString());
			return Response.created(location).build();
		} finally {
			span.finish();
		}
	}

	@Override
	public Response replace(HttpHeaders headers, UriInfo uriInfo, Audience audience) {
		Span span = this.tracer.buildSpan("AudienceResource#replace").start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			this.audienceService.replaceAudience(audience);
			return Response.noContent().build();
		} finally {
			span.finish();
		}
	}

	@Override
	public Response delete(UriInfo uriInfo, String uuid) {
		Span span = this.tracer.buildSpan("AudienceResource#delete").start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			this.audienceService.deleteAudience(UUID.fromString(uuid));
			return Response.ok().build();
		} finally {
			span.finish();
		}
	}
}
