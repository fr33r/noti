package api.interceptors;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WriterInterceptorContext extends javax.ws.rs.ext.WriterInterceptorContext {

  /**
   * Get the entity bytes.
   *
   * @return A byte array containing the entity.
   * @throws IOException {@inheritDoc}
   */
  byte[] getEntityBytes() throws IOException;

  HttpServletRequest getRequest();

  HttpServletResponse getResponse();
}
