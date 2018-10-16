package api;

import com.fasterxml.jackson.jaxrs.yaml.YAMLMediaTypes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/")
public interface NotiResource {

  @GET
  @Produces({
    MediaType.APPLICATION_JSON,
    MediaType.APPLICATION_XML,
    YAMLMediaTypes.APPLICATION_JACKSON_YAML,
    YAMLMediaTypes.TEXT_JACKSON_YAML,
    "application/vnd.siren+json",
    "application/x-yaml",
    "text/x-yaml",
    "text/vnd.yaml"
  })
  Response get(@Context HttpHeaders headers, @Context UriInfo uriInfo);
}
