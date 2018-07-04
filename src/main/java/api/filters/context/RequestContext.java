package api.filters.context;

import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.net.URI;
import java.util.Date;
import java.io.InputStream;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.HttpHeaders;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;

public class RequestContext implements api.filters.RequestContext {

  private final ContainerRequestContext containerRequestContext;

  public RequestContext(final ContainerRequestContext containerRequestContext) {
    this.containerRequestContext = containerRequestContext;
  }

  @Override
  public void abortWith(Response response) {
	this.containerRequestContext.abortWith(response);
  }

  @Override
  public List<String> getAcceptableCharsets() {
    String acceptCharset = this.getHeaderString(HttpHeaders.ACCEPT_CHARSET);
    if (acceptCharset == null || acceptCharset.isEmpty()) {
      return Collections.singletonList("*");
    }
    // TODO - implement this algorithm.
    return null;
  }

  @Override
  public List<String> getAcceptableEncodings() {
    String acceptEncoding = this.getHeaderString(HttpHeaders.ACCEPT_ENCODING);
    if (acceptEncoding == null || acceptEncoding.isEmpty()) {
      return Collections.singletonList("*");
    }
    // TODO - implement this algorithm.
    return null;
  }

  @Override
  public List<Locale> getAcceptableLanguages() {
    return this.containerRequestContext.getAcceptableLanguages();
  }

  @Override
  public List<MediaType> getAcceptableMediaTypes() {
    return this.containerRequestContext.getAcceptableMediaTypes();
  }

  @Override
  public Map<String, Cookie> getCookies() {
    return this.containerRequestContext.getCookies();
  }

  @Override
  public Date getDate() {
    return this.containerRequestContext.getDate();
  }

	@Override
	public String getEncoding() {
		return this.getHeaderString(HttpHeaders.CONTENT_ENCODING);
	}

  @Override
  public InputStream getEntityStream() {
    return this.containerRequestContext.getEntityStream();
  }

  @Override
  public MultivaluedMap<String, String> getHeaders() {
    return this.containerRequestContext.getHeaders();
  }

  @Override
  public String getHeaderString(String name) {
    return this.containerRequestContext.getHeaderString(name);
  }

  @Override
  public Locale getLanguage() {
    return this.containerRequestContext.getLanguage();
  }

  @Override
  public int getLength() {
    return this.containerRequestContext.getLength();
  }

  @Override
  public MediaType getMediaType() {
    return this.containerRequestContext.getMediaType();
  }

  @Override
  public String getMethod() {
    return this.containerRequestContext.getMethod();
  }

  @Override
  public Object getProperty(String name) {
    return this.containerRequestContext.getProperty(name);
  }

  @Override
  public Collection<String> getPropertyNames() {
    return this.containerRequestContext.getPropertyNames();
  }

  @Override
  public Request getRequest() {
    return this.containerRequestContext.getRequest();
  }

  @Override
  public URI getRequestUri() {
    return this.getUriInfo().getRequestUri();
  }

  @Override
  public SecurityContext getSecurityContext() {
    return this.containerRequestContext.getSecurityContext();
  }

  @Override
  public UriInfo getUriInfo() {
    return this.getUriInfo();
  }

  @Override
  public boolean hasEntity() {
    return this.containerRequestContext.hasEntity();
  }

  @Override
  public boolean methodIs(String method) {
    return this.getMethod().equalsIgnoreCase(method);
  }

  @Override
  public void removeProperty(String name) {
    this.containerRequestContext.removeProperty(name);
  }

  @Override
  public void setEntityStream(InputStream inputStream) {
    this.containerRequestContext.setEntityStream(inputStream);
  }

  @Override
  public void setMethod(String method) {
    this.containerRequestContext.setMethod(method);
  }

  @Override
  public void setProperty(String name, Object object) {
    this.containerRequestContext.setProperty(name, object);
  }

  @Override
  public void setRequestUri(URI requestUri) {
    this.containerRequestContext.setRequestUri(requestUri);
  }

  @Override
  public void setRequestUri(URI baseUri, URI requestUri) {
    this.containerRequestContext.setRequestUri(baseUri, requestUri);
  }

  @Override
  public void setSecurityContext(SecurityContext context) {
    this.containerRequestContext.setSecurityContext(context);
  }
}
