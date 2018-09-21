package api.resources;

import api.representations.RepresentationFactory;
import io.opentracing.Tracer;
import java.util.Map;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public abstract class Resource {

  private final Map<MediaType, RepresentationFactory> representationIndustry;
  private final Tracer tracer;

  public Resource(Map<MediaType, RepresentationFactory> representationIndustry, Tracer tracer) {
    this.representationIndustry = representationIndustry;
    this.tracer = tracer;
  }

  Tracer getTracer() {
    return this.tracer;
  }

  public RepresentationFactory getRepresentationFactory(HttpHeaders headers) {
    RepresentationFactory representationFactory = null;
    for (MediaType acceptableMediaType : headers.getAcceptableMediaTypes()) {
      if (this.representationIndustry.containsKey(acceptableMediaType)) {
        representationFactory = this.representationIndustry.get(acceptableMediaType);
        break;
      }
    }
    return representationFactory;
  }
}
