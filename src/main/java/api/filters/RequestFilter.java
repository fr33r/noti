package api.filters;

import api.filters.context.RequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import java.io.IOException;

public abstract class RequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		this.filter(new RequestContext(requestContext));
	}

	public abstract void filter(api.filters.RequestContext requestContext) throws IOException;
}
