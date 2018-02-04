package api.filters;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Request;
import javax.ws.rs.ext.Provider;

@Provider
public class VaryFilter implements ContainerResponseFilter {

	public void filter(
		ContainerRequestContext requestContext,
		ContainerResponseContext responseContext
	) throws IOException {

		if(responseContext.getStatus() >= 400) { return; }

		Request request = requestContext.getRequest();

		if(request.getMethod().equalsIgnoreCase("GET")){
			responseContext.getHeaders().add("Vary", "Accept");
		}
	}
}
