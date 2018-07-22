package api.interceptors.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

public final class WriterInterceptorContext implements api.interceptors.WriterInterceptorContext {

  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final javax.ws.rs.ext.WriterInterceptorContext context;

  public WriterInterceptorContext(
      HttpServletRequest request,
      HttpServletResponse response,
      javax.ws.rs.ext.WriterInterceptorContext context) {
    this.request = request;
    this.response = response;
    this.context = context;
  }

  @Override
  public byte[] getEntityBytes() throws IOException {
    OutputStream os = this.getOutputStream();
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      this.setOutputStream(baos);
      this.proceed();
      byte[] entityBytes = baos.toByteArray();
      os.write(entityBytes);
      return entityBytes;
    } finally {
      this.setOutputStream(os);
    }
  }

  @Override
  public HttpServletRequest getRequest() {
    return this.request;
  }

  @Override
  public HttpServletResponse getResponse() {
    return this.response;
  }

  @Override
  public Annotation[] getAnnotations() {
    return this.context.getAnnotations();
  }

  @Override
  public Type getGenericType() {
    return this.context.getGenericType();
  }

  @Override
  public MediaType getMediaType() {
    return this.context.getMediaType();
  }

  @Override
  public Object getProperty(String name) {
    return this.context.getProperty(name);
  }

  @Override
  public Collection<String> getPropertyNames() {
    return this.context.getPropertyNames();
  }

  @Override
  public Class<?> getType() {
    return this.context.getType();
  }

  @Override
  public void removeProperty(String name) {
    this.context.removeProperty(name);
  }

  @Override
  public void setAnnotations(Annotation[] annotations) {
    this.context.setAnnotations(annotations);
  }

  @Override
  public void setGenericType(Type genericType) {
    this.context.setGenericType(genericType);
  }

  @Override
  public void setMediaType(MediaType mediaType) {
    this.context.setMediaType(mediaType);
  }

  @Override
  public void setProperty(String name, Object object) {
    this.context.setProperty(name, object);
  }

  @Override
  public void setType(Class<?> type) {
    this.context.setType(type);
  }

  @Override
  public Object getEntity() {
    return this.context.getEntity();
  }

  @Override
  public MultivaluedMap<String, Object> getHeaders() {
    return this.context.getHeaders();
  }

  @Override
  public OutputStream getOutputStream() {
    return this.context.getOutputStream();
  }

  @Override
  public void proceed() throws IOException, WebApplicationException {
    this.context.proceed();
  }

  @Override
  public void setEntity(Object entity) {
    this.context.setEntity(entity);
  }

  @Override
  public void setOutputStream(OutputStream outputStream) {
    this.context.setOutputStream(outputStream);
  }
}
