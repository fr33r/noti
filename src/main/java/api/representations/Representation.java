package api.representations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;

public class Representation {

  private MediaType mediaType;
  private URI location;
  private String encoding;
  private Date lastModified;
  private Locale language;

  private Representation() {}

  // the idea here is to create a representation
  // that doesn't have any metadata - although
  // a media type must be provided.
  protected Representation(final MediaType mediaType) {
    this.mediaType = mediaType;
    this.location = null;
    this.encoding = null;
    this.language = null;
    this.lastModified = null;
  }

  public abstract static class Builder {
    private MediaType mediaType;
    private URI location;
    private String encoding;
    private Date lastModified;
    private Locale language;

    public Builder(MediaType mediaType) {
      this.mediaType(mediaType);
    }

    private Builder mediaType(MediaType mediaType) {
      this.mediaType = mediaType;
      return this;
    }

    protected MediaType mediaType() {
      return this.mediaType;
    }

    public Builder location(URI location) {
      this.location = location;
      return this;
    }

    protected URI location() {
      return this.location;
    }

    public Builder encoding(String encoding) {
      this.encoding = encoding;
      return this;
    }

    protected String encoding() {
      return this.encoding;
    }

    public Builder lastModified(Date lastModified) {
      this.lastModified = lastModified;
      return this;
    }

    protected Date lastModified() {
      return lastModified;
    }

    public Builder language(Locale language) {
      this.language = language;
      return this;
    }

    protected Locale language() {
      return this.language;
    }

    public abstract Representation build();
  }

  @JsonIgnore
  public MediaType getMediaType() {
    return this.mediaType;
  }

  /**
   * Retreives the canonical URI used to identify this representation.
   *
   * <p><see> The <a href='https://tools.ietf.org/html/rfc7231#section-3.1.4.2'>Content-Location</a>
   * header as described in RFC7231. </see>
   *
   * @return The canonical URI identifying this representation.
   */
  @JsonIgnore
  public URI getLocation() {
    return this.location;
  }

  @JsonIgnore
  public void setLocation(URI location) {
    this.location = location;
  }

  /**
   * Retrieves the Entity Tag (ETag) for the representation.
   *
   * <p><see> The <a href='https://tools.ietf.org/html/rfc7232#section-2.3'>ETag</a> header as
   * described in RFC7231. </see>
   *
   * @return The ETag for the representation.
   */
  @JsonIgnore
  public EntityTag getEntityTag() {
    // should i really use JSON serialization here?
    // what if i just mark this as abstract and then
    // the child classes are responsbile for including
    // all parts that should play into the ETag?
    try {
      ObjectMapper mapper = new ObjectMapper();
      byte[] serializedBytes = mapper.writeValueAsBytes(this);
      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] hashedBytes = digest.digest(serializedBytes);
      String hashedBytesBase64 = Base64.getEncoder().encodeToString(hashedBytes);
      return new EntityTag(hashedBytesBase64);
    } catch (Exception x) {
      throw new RuntimeException(x);
    }
  }

  /**
   * Retrieves the coding scheme used for the representation.
   *
   * <p><see> The <a href='https://tools.ietf.org/html/rfc7231#section-3.1.2.2'>Content-Encoding</a>
   * header as described in RFC7231. </see> <see> <a
   * href='https://tools.ietf.org/html/rfc7231#section-3.1.2.1'>Content codings</a> as described in
   * RFC7231. </see>
   *
   * @return The coding scheme used to encode the representation.
   */
  @JsonIgnore
  public String getEncoding() {
    return this.encoding;
  }

  /**
   * Alters the coding scheme that was used to encode used for the representation.
   *
   * <p><see> The <a href='https://tools.ietf.org/html/rfc7231#section-3.1.2.2'>Content-Encoding</a>
   * header as described in RFC7231. </see> <see> <a
   * href='https://tools.ietf.org/html/rfc7231#section-3.1.2.1'>Content codings</a> as described in
   * RFC7231. </see>
   *
   * @param encoding The name of the coding scheme used to encode the representation.
   */
  @JsonIgnore
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  /**
   * Retrieves the language of the target audience of the representation.
   *
   * <p><see> The <a href='https://tools.ietf.org/html/rfc7231#section-3.1.3.2'>Content-Language</a>
   * header as discussed in RFC7231. </see> <see> <a
   * href='https://tools.ietf.org/html/rfc7231#section-3.1.3.1'>Language tags</a> as discussed in
   * RFC7231. </see>
   *
   * @return The language of the target audience of the representation.
   */
  @JsonIgnore
  public Locale getLanguage() {
    return this.language;
  }

  /**
   * Alters the language of the target audience of the representation.
   *
   * <p><see> The <a href='https://tools.ietf.org/html/rfc7231#section-3.1.3.2'>Content-Language</a>
   * header as discussed in RFC7231. </see> <see> <a
   * href='https://tools.ietf.org/html/rfc7231#section-3.1.3.1'>Language tags</a> as discussed in
   * RFC7231. </see>
   *
   * @param language The language for the target audience of the representation.
   */
  @JsonIgnore
  public void setLanguage(Locale language) {
    this.language = language;
  }

  /**
   * Retrieves the date and time that this representation was last modified. Note - the date and
   * time that the representation was last modified might not be known, since this information may
   * not be tracked for the representation (in the event that a representation metadata store cannot
   * be accessed, for example). Therefore, client code should not expect every representation to
   * return a not non-null value for this method. Note - the date and time by the HTTP RFC only has
   * a one-second time granularity. It is strongly suggested that HTTP clients utilize ETag as it
   * should always be available, (every representation will have an ETag) and it's more precise.
   *
   * <p><see> reference for Last-Modified header. </see> <see> reference for ETag header. </see>
   * <see> reference for HTTP RFC. </see> <see> reference for date and time format. </see>
   *
   * @return The date and time the representation was last modified (in X format). In the event that
   *     a date and time of when the representation was last modified is not known, a value of
   *     {@code null} will be returned.
   */
  @JsonIgnore
  public Date getLastModified() {
    return this.lastModified;
  }

  /**
   * Alters the data and time that the this representation was last modified.
   *
   * @param lastModified The date and time that the representation was last modified.
   */
  @JsonIgnore
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }
}
