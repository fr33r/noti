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
    for (MediaType acceptableMediaType : headers.getAcceptableMediaTypes()) {
      for (Map.Entry<MediaType, RepresentationFactory> factoryEntry :
          this.representationIndustry.entrySet()) {
        if (acceptableMediaType.isCompatible(factoryEntry.getKey())) {
          return factoryEntry.getValue();
        }
      }
    }
    return null;
  }
}
