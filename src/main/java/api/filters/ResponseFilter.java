package api.filters;

// TODO - change to:
//  api.filter.ResponseContext;
//		api.filter.context.ResponseContext;
//  api.filter.RequestContext;
//		api.filter.context.RequestContext;
import api.filters.context.RequestContext;
import api.filters.context.ResponseContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

public abstract class ResponseFilter implements ContainerResponseFilter {

  @Override
  public void filter(
      ContainerRequestContext containerRequestContext,
      ContainerResponseContext containerResponseContext) {
    // TODO - replace with factory calls.
    RequestContext requestContext = new RequestContext(containerRequestContext);
    ResponseContext responseContext = new ResponseContext(containerResponseContext);
    this.filter(requestContext, responseContext);
  }

  public abstract void filter(
      api.filters.RequestContext requestContext, api.filters.ResponseContext responseContext);
}
