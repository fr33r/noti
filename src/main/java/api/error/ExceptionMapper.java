package api.error;

import api.representations.RepresentationFactory;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public abstract class ExceptionMapper<T extends Exception>
    implements javax.ws.rs.ext.ExceptionMapper<T> {

  @Context private HttpHeaders headers;
  @Context private UriInfo uriInfo;
  private Map<MediaType, RepresentationFactory> representationIndustry;

  @Inject
  public ExceptionMapper(Map<MediaType, RepresentationFactory> representationIndustry) {
    this.representationIndustry = representationIndustry;
  }

  public abstract Response toResponse(T x);

  private RepresentationFactory getRepresentationFactory(HttpHeaders headers) {
    RepresentationFactory representationFactory = null;
    for (MediaType acceptableMediaType : headers.getAcceptableMediaTypes()) {
      if (this.representationIndustry.containsKey(acceptableMediaType)) {
        representationFactory = this.representationIndustry.get(acceptableMediaType);
        break;
      }
    }
    return representationFactory;
  }

  UriInfo getUriInfo() {
    return this.uriInfo;
  }

  RepresentationFactory getRepresentationFactory() {
    return this.getRepresentationFactory(this.headers);
  }
}
