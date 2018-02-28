package api.resources;

import java.net.URI;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import api.representations.Audience;
import application.AudienceService;

public class AudienceResource implements api.AudienceResource {

	private final AudienceService audienceService;

	@Inject
	public AudienceResource(
		AudienceService audienceService
	) {
		this.audienceService = audienceService;
	}

	@Override
	public Response get(HttpHeaders headers, UriInfo uriInfo, String uuid) {
		Audience audience = this.audienceService.getAudience(UUID.fromString(uuid));
		return Response.ok(audience).build();
	}

	@Override
	public Response createAndAppend(HttpHeaders headers, UriInfo uriInfo, Audience audience) {
		UUID audienceUUID = this.audienceService.createAudience(audience);
		URI location =
			UriBuilder
				.fromUri(uriInfo.getRequestUri())
				.path("/{uuid}/")
				.build(audienceUUID.toString());
		return Response.created(location).build();
	}

	@Override
	public Response replace(HttpHeaders headers, UriInfo uriInfo, Audience audience) {
		this.audienceService.replaceAudience(audience);
		return Response.noContent().build();
	}

	@Override
	public Response delete(UriInfo uriInfo, String uuid) {
		this.audienceService.deleteAudience(UUID.fromString(uuid));
		return Response.ok().build();
	}
}
