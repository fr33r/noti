package api.error;

import api.representations.Representation;
import api.representations.RepresentationFactory;
import application.NotFoundException;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public final class NotFoundExceptionMapper extends ExceptionMapper<NotFoundException> {

  @Inject
  public NotFoundExceptionMapper(Map<MediaType, RepresentationFactory> representationIndustry) {
    super(representationIndustry);
  }

  @Override
  public Response toResponse(NotFoundException x) {
    RepresentationFactory representationFactory = this.getRepresentationFactory();
    Representation representation =
        representationFactory.createErrorRepresentation(this.getUriInfo().getRequestUri(), null, x);
    return Response.status(Response.Status.NOT_FOUND).entity(representation).build();
  }
}
