package api.filters;

import api.filters.context.RequestContext;
import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

public abstract class RequestFilter implements ContainerRequestFilter {

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    this.filter(new RequestContext(requestContext));
  }

  public abstract void filter(api.filters.RequestContext requestContext) throws IOException;
}
