package api.filters.context;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

public class RequestContext implements api.filters.RequestContext {

  private final ContainerRequestContext containerRequestContext;

  public RequestContext(final ContainerRequestContext containerRequestContext) {
    this.containerRequestContext = containerRequestContext;
  }

  private final class WeightedHeaderValue implements Comparable<WeightedHeaderValue> {

    /**
     * @see <a href='https://tools.ietf.org/html/rfc7231#section-5.3.1'>RFC 7231 Section 5.3.1</a>
     */
    private final class Quality implements Comparable<Quality> {

      private final float qValue;

      public Quality(String qValue) {
        this.qValue = new Float(qValue).floatValue();
        if (this.qValue > 1.0 || this.qValue < 0.0) {
          throw new IllegalStateException("Quality value must be between 0.0 and 1.0");
        }
      }

      public float getValue() {
        return this.qValue;
      }

      @Override
      public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Quality quality = (Quality) obj;
        return this.getValue() == quality.getValue();
      }

      @Override
      public int hashCode() {
        final float prime = 17.0f;
        return (int) (this.qValue * prime);
      }

      @Override
      public int compareTo(Quality quality) {
        if (quality == null) return 1;
        if (quality.equals(this)) return 0;
        return quality.getValue() < this.getValue() ? 1 : -1;
      }
    }

    private final String headerValue;
    private final Quality quality;

    public WeightedHeaderValue(String headerValue) {
      String[] parts = headerValue.split(";");
      this.headerValue = parts[0];
      if (parts.length > 1) {
        this.quality = new Quality(parts[1]);
      } else {
        this.quality = null;
      }
    }

    public String getHeaderValue() {
      return this.headerValue;
    }

    public Quality getQuality() {
      return this.quality;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null || obj.getClass() != this.getClass()) return false;
      WeightedHeaderValue weightedHeaderValue = (WeightedHeaderValue) obj;
      boolean hasSameHeaderValue =
          this.getHeaderValue().equals(weightedHeaderValue.getHeaderValue());
      boolean hasSameQuality =
          this.getQuality() == null && weightedHeaderValue.getQuality() == null
              || this.getQuality() != null
                  && weightedHeaderValue.getQuality() != null
                  && this.getQuality().equals(weightedHeaderValue.getQuality());
      return hasSameQuality && hasSameHeaderValue;
    }

    @Override
    public int hashCode() {
      int hashCode = 1;
      final int prime = 17;
      hashCode = hashCode * prime + this.getHeaderValue().hashCode();
      if (this.getQuality() != null) {
        hashCode = hashCode * prime + this.getQuality().hashCode();
      }
      return hashCode;
    }

    @Override
    public int compareTo(WeightedHeaderValue headerValue) {
      if (headerValue == null) return 1;
      if (headerValue.equals(this)) return 0;
      Quality quality = this.getQuality();
      if (quality == null) return 1;
      return headerValue.getQuality().getValue() < this.getQuality().getValue() ? 1 : -1;
    }
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
    List<WeightedHeaderValue> weightedHeaderValues = this.asWeightedHeaderValues(acceptCharset);
    Collections.sort(weightedHeaderValues);
    List<String> weightedHeaderValueStrings = new ArrayList<>();
    for (WeightedHeaderValue weightedHeaderValue : weightedHeaderValues) {
      weightedHeaderValueStrings.add(weightedHeaderValue.getHeaderValue());
    }
    return Collections.unmodifiableList(weightedHeaderValueStrings);
  }

  private WeightedHeaderValue asWeightedHeaderValue(String weightedHeaderValue) {
    return new WeightedHeaderValue(weightedHeaderValue);
  }

  private List<WeightedHeaderValue> asWeightedHeaderValues(String headerAsString) {
    List<WeightedHeaderValue> weightedHeaderValues = new ArrayList<>();
    String[] parts = headerAsString.split(",");
    for (String part : parts) {
      weightedHeaderValues.add(this.asWeightedHeaderValue(part));
    }
    return weightedHeaderValues;
  }

  @Override
  public List<String> getAcceptableEncodings() {
    String acceptEncoding = this.getHeaderString(HttpHeaders.ACCEPT_ENCODING);
    if (acceptEncoding == null || acceptEncoding.isEmpty()) {
      return Collections.singletonList("*");
    }
    List<WeightedHeaderValue> weightedHeaderValues = this.asWeightedHeaderValues(acceptEncoding);
    Collections.sort(weightedHeaderValues);
    List<String> weightedHeaderValueStrings = new ArrayList<>();
    for (WeightedHeaderValue weightedHeaderValue : weightedHeaderValues) {
      weightedHeaderValueStrings.add(weightedHeaderValue.getHeaderValue());
    }
    return Collections.unmodifiableList(weightedHeaderValueStrings);
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
    return this.containerRequestContext.getUriInfo();
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
