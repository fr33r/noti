package api.interceptors;

import api.interceptors.context.WriterInterceptorContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

public abstract class WriterInterceptor implements javax.ws.rs.ext.WriterInterceptor {

  @Context private HttpServletRequest request;

  @Context private HttpServletResponse response;

  @Override
  public void aroundWriteTo(javax.ws.rs.ext.WriterInterceptorContext writerInterceptorContext) {
    this.aroundWriteTo(new WriterInterceptorContext(request, response, writerInterceptorContext));
  }

  public abstract void aroundWriteTo(WriterInterceptorContext writerInterceptorContext);
}
