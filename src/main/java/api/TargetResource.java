package api;

import api.representations.json.Target;
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

/**
 * Defines the abstraction that exposes various HTTP operations on the target resource.
 *
 * @author jonfreer
 */
@Path("/targets")
public interface TargetResource {

  /**
   * Handles HTTP GET requests for the targte with the unique identifier provided.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param uuid The universally unique identifier for the target resource being retrieved.
   * @return The HTTP {@link Response}, including the representation of the requested target
   *     resource.
   */
  @GET
  @Path("{uuid}")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/vnd.siren+json"})
  Response get(
      @Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("uuid") String uuid);

  /**
   * Handles HTTP POST requests for the collection of target resources.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param target The representation of the target resource to be created.
   * @return The HTTP {@link Response}, including the representation of the requested target
   *     resource.
   */
  @POST
  @Consumes({MediaType.APPLICATION_JSON})
  Response createAndAppend(@Context HttpHeaders headers, @Context UriInfo uriInfo, Target target);

  /**
   * Handles HTTP PUT requests for the target resource with the unique identifier provided.
   *
   * @param headers The headers from the HTTP request.
   * @param uriInfo Information about the URI of the HTTP request.
   * @param target The representation of the target resource to replace the current existing state.
   * @return The HTTP {@link Response}, including the representation of the requested target
   *     resource.
   */
  @PUT
  @Path("{uuid}")
  @Consumes({MediaType.APPLICATION_JSON})
  Response replace(@Context HttpHeaders headers, @Context UriInfo uriInfo, Target target);

  /**
   * Handles HTTP DELETE requests for the target resource with the unique identifier provided.
   *
   * @param uuid The universally unique identifier for the target resource being retrieved.
   * @param uriInfo Information about the URI of the HTTP request.
   * @return The HTTP {@link Response}, including the representation of the requested target
   *     resource.
   */
  @DELETE
  @Path("{uuid}")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/vnd.siren+json"})
  Response delete(@Context UriInfo uriInfo, @PathParam("uuid") String uuid);
}
