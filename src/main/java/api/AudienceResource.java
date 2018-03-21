package api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import api.representations.Audience;

@Path("/audiences")
public interface AudienceResource {

	@GET
	@Path("{uuid}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/vnd.siren+json"})
	Response get(
		@Context HttpHeaders headers,
		@Context UriInfo uriInfo,
		@PathParam("uuid") String uuid
	);

	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	Response createAndAppend(
		@Context HttpHeaders headers,
		@Context UriInfo uriInfo,
		Audience audience
	);

	@PUT
	@Path("{uuid}")
	@Consumes({MediaType.APPLICATION_JSON})
	Response replace(
		@Context HttpHeaders headers,
		@Context UriInfo uriInfo,
		Audience audience
	);

	@DELETE
	@Path("{uuid}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/vnd.siren+json"})
	Response delete(
		@Context UriInfo uriInfo,
		@PathParam("uuid") String uuid
	);
}
