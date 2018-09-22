package api.error;

import api.representations.Representation;
import api.representations.RepresentationFactory;
import application.ApplicationException;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public final class ApplicationExceptionMapper extends ExceptionMapper<ApplicationException> {

  @Inject
  public ApplicationExceptionMapper(Map<MediaType, RepresentationFactory> representationIndustry) {
    super(representationIndustry);
  }

  @Override
  public Response toResponse(ApplicationException x) {
    RepresentationFactory representationFactory = this.getRepresentationFactory();
    Representation representation =
        representationFactory.createErrorRepresentation(this.getUriInfo().getRequestUri(), null, x);
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(representation).build();
  }
}
