package api.resources;

import java.net.URI;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import application.TargetService;
import api.representations.Target;

/**
 * Represents a target resource for this RESTful API.
 *
 * @author Jon Freer
 */
public final class TargetResource implements api.TargetResource {

	private final TargetService targetService;

	/**
	 * Constructs a new {@link TargetResource} instance.
	 */
	@Inject
	public TargetResource(
		TargetService targetService
	) {
		this.targetService = targetService;
	}

	/**
	 * Handles HTTP GET requests for the target with the unique identifier provided.
	 *
	 * @param headers The HTTP headers from the request.
	 * @param uriInfo Information about the request URI.
	 * @param uuid The unique identifier for the target resource being retrieved.
	 * @return An HTTP response with the status code of 200, along with a body containing the representation of the resource.
	 */
	public Response get(HttpHeaders headers, UriInfo uriInfo, String uuid) {
		Target target = this.targetService.getTarget(UUID.fromString(uuid));
		return Response.ok(target).build();
	}

	/**
	 * Handles HTTP POST requests to the collection of target resources.
	 *
	 * @param headers The HTTP headers provided in the request.
	 * @param uriInfo Information about the request URI.
	 * @param target The desired representation of the target resource to be created and appended to the target resource collection.
	 * @return An HTTP response with a status code of 201.
	 */
	public Response createAndAppend(HttpHeaders headers, UriInfo uriInfo, Target target) {

		UUID uuid = this.targetService.createTarget(target);
		URI location =
			UriBuilder
				.fromUri(uriInfo.getRequestUri())
				.path("/{uuid}/")
				.build(uuid.toString());
		return Response.created(location).build();
	}

	/**
	 * Handles HTTP PUT requests for the target provided.
	 *
	 * @param headers The HTTP headers provided within the request.
	 * @param uriInfo Information about the request URI.
	 * @param target The desired representation of the target resource.
	 * @return An HTTP response without a body and with status code of 204.
	 */
	public Response replace(HttpHeaders headers, UriInfo uriInfo, Target target) {
		this.targetService.replaceTarget(target);
		return Response.noContent().build();
	}

	/**
	 * Handles HTTP DELETE requests for the target with the unique identifier provided.
	 *
	 * @param uriInfo Information about the request URI, so that it can be leveraged when construcing the response.
	 * @param uuid The unique identifier for the resource being deleted.
	 * @return An HTTP response without a body and with a status code of 204.
	 */
	public Response delete(UriInfo uriInfo, String uuid) {
		this.targetService.deleteTarget(UUID.fromString(uuid));
		return Response.noContent().build();
	}
}
