package api.filters.context;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

/**
 * Provides response-specific information for a response filter.
 *
 * @author Jon Freer
 */
public final class ResponseContext implements api.filters.ResponseContext {

  private final ContainerResponseContext containerResponseContext;

  public ResponseContext(final ContainerResponseContext containerResponseContext) {
    this.containerResponseContext = containerResponseContext;
  }

  @Override
  public Set<String> getAllowedMethods() {
    return this.containerResponseContext.getAllowedMethods();
  }

  @Override
  public Map<String, NewCookie> getCookies() {
    return this.containerResponseContext.getCookies();
  }

  @Override
  public Date getDate() {
    return this.containerResponseContext.getDate();
  }

  @Override
  public List<String> getEncodings() {
    String contentEncoding = this.getHeaderString(HttpHeaders.CONTENT_ENCODING);
    if (contentEncoding == null || contentEncoding.isEmpty()) {
      return null;
    }

    String[] parts = contentEncoding.split(",");
    List<String> encodings = new ArrayList<String>();
    for (String part : parts) {
      encodings.add(part);
    }
    return encodings;
  }

  @Override
  public Object getEntity() {
    return this.containerResponseContext.getEntity();
  }

  @Override
  public Annotation[] getEntityAnnotations() {
    return this.containerResponseContext.getEntityAnnotations();
  }

  @Override
  public Class<?> getEntityClass() {
    return this.containerResponseContext.getEntityClass();
  }

  @Override
  public OutputStream getEntityStream() {
    return this.containerResponseContext.getEntityStream();
  }

  @Override
  public EntityTag getEntityTag() {
    return this.containerResponseContext.getEntityTag();
  }

  @Override
  public Type getEntityType() {
    return this.containerResponseContext.getEntityType();
  }

  @Override
  public MultivaluedMap<String, Object> getHeaders() {
    return this.containerResponseContext.getHeaders();
  }

  @Override
  public String getHeaderString(String name) {
    return this.containerResponseContext.getHeaderString(name);
  }

  @Override
  public Locale getLanguage() {
    return this.containerResponseContext.getLanguage();
  }

  @Override
  public Date getLastModified() {
    return this.containerResponseContext.getLastModified();
  }

  @Override
  public int getLength() {
    return this.containerResponseContext.getLength();
  }

  @Override
  public Link getLink(String relation) {
    return this.containerResponseContext.getLink(relation);
  }

  @Override
  public Link.Builder getLinkBuilder(String relation) {
    return this.containerResponseContext.getLinkBuilder(relation);
  }

  @Override
  public Set<Link> getLinks() {
    return this.containerResponseContext.getLinks();
  }

  @Override
  public URI getLocation() {
    return this.containerResponseContext.getLocation();
  }

  @Override
  public MediaType getMediaType() {
    return this.containerResponseContext.getMediaType();
  }

  @Override
  public int getStatus() {
    return this.containerResponseContext.getStatus();
  }

  @Override
  public Response.StatusType getStatusInfo() {
    return this.containerResponseContext.getStatusInfo();
  }

  @Override
  public MultivaluedMap<String, String> getStringHeaders() {
    return this.containerResponseContext.getStringHeaders();
  }

  @Override
  public boolean hasEntity() {
    return this.containerResponseContext.hasEntity();
  }

  @Override
  public boolean hasLink(String relation) {
    return this.containerResponseContext.hasLink(relation);
  }

  @Override
  public void setLastModified(Date lastModified) {
    this.getHeaders().add(HttpHeaders.LAST_MODIFIED, lastModified);
  }

  @Override
  public void setEntity(Object entity) {
    this.containerResponseContext.setEntity(entity);
  }

  @Override
  public void setEntity(Object entity, Annotation[] annotations, MediaType mediaType) {
    this.containerResponseContext.setEntity(entity, annotations, mediaType);
  }

  @Override
  public void setEntityStream(OutputStream outputStream) {
    this.containerResponseContext.setEntityStream(outputStream);
  }

  @Override
  public void setEntityTag(EntityTag entityTag) {
    this.getHeaders().add(HttpHeaders.ETAG, entityTag);
  }

  @Override
  public void setStatus(int code) {
    this.containerResponseContext.setStatus(code);
  }

  @Override
  public void setStatusInfo(Response.StatusType statusInfo) {
    this.containerResponseContext.setStatusInfo(statusInfo);
  }

  @Override
  public boolean statusIs(int code) {
    return this.getStatus() == code;
  }

  @Override
  public boolean statusIsClientError() {
    return this.getStatus() / 100 == 4;
  }

  @Override
  public boolean statusIsInformational() {
    return this.getStatus() / 100 == 1;
  }

  @Override
  public boolean statusIsRedirection() {
    return this.getStatus() / 100 == 3;
  }

  @Override
  public boolean statusIsServerError() {
    return this.getStatus() / 100 == 5;
  }

  @Override
  public boolean statusIsSuccessful() {
    return this.getStatus() / 100 == 2;
  }
}
