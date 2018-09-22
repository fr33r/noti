package api.representations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A resource representation as described in the <a
 * href='https://tools.ietf.org/html/rfc7231#section-3'>HTTP 1.1 specification. </a>
 *
 * <p>See <a href='https://tools.ietf.org/html/rfc7231#section-3'>RFC7231 Section 3 </a>
 *
 * @author Jon Freer
 */
@XmlSeeAlso({
  api.representations.RepresentationCollection.class,
  api.representations.xml.Notification.class,
  api.representations.xml.Audience.class,
  api.representations.xml.Target.class,
  api.representations.xml.Message.class,
  api.representations.xml.Error.class
})
public abstract class Representation {

  private MediaType mediaType;
  private URI location;
  private String encoding;
  private Date lastModified;
  private Locale language;

  protected Representation() {}

  /**
   * Constructs a new {@link Representation}.
   *
   * @param mediaType The media type of the {@link Representation}.
   */
  protected Representation(final MediaType mediaType) {
    this.mediaType = mediaType;
    this.location = null;
    this.encoding = null;
    this.language = null;
    this.lastModified = null;
  }

  /**
   * A builder of {@link Representation} instances.
   *
   * @author Jon Freer
   */
  public abstract static class Builder {
    private MediaType mediaType;
    private URI location;
    private String encoding;
    private Date lastModified;
    private Locale language;

    /**
     * Constructs a builder of {@link Representation} instances.
     *
     * @param mediaType The desired media type of the {@link Representation} being built.
     */
    public Builder(MediaType mediaType) {
      this.mediaType(mediaType);
    }

    /**
     * Sets the media type of the {@link Representation} being built.
     *
     * @param mediaType The desired media type of the {@link Representation} being built.
     * @return The updated {@link Representation} builder.
     */
    private Builder mediaType(MediaType mediaType) {
      this.mediaType = mediaType;
      return this;
    }

    /**
     * Retrieves the media type of the {@link Representation} being built.
     *
     * @return The media type of the {@link Representation} being built.
     */
    protected MediaType mediaType() {
      return this.mediaType;
    }

    /**
     * Sets the content location of the {@link Representation} being built.
     *
     * @param location The desired content location of the {@link Representation} being built.
     * @return The updated {@link Representation} builder.
     */
    public Builder location(URI location) {
      this.location = location;
      return this;
    }

    /**
     * Retrieves the content location of the {@link Representation} being built.
     *
     * @return The content location of the {@link Representation} being built.
     */
    protected URI location() {
      return this.location;
    }

    /**
     * Sets the content encoding of the {@link Representation} being built.
     *
     * @param encoding The desired content encoding of the {@link Representation} being built.
     * @return The updated {@link Representation} builder.
     */
    public Builder encoding(String encoding) {
      this.encoding = encoding;
      return this;
    }

    /**
     * Retrieves the content encoding of the {@link Representation} being built.
     *
     * @return The content encoding of the {@link Representation} being built.
     */
    protected String encoding() {
      return this.encoding;
    }

    /**
     * Sets the last modified date and time of the {@link Representation} being built.
     *
     * @param lastModified The desired date and time indicating the when the {@link Representation}
     *     was last modified.
     * @return The updated {@link Representation} builder.
     */
    public Builder lastModified(Date lastModified) {
      this.lastModified = lastModified;
      return this;
    }

    /**
     * Retrieves the last modified date and time of the {@link Representation} being built.
     *
     * @return The last modified date and time of the {@link Representation} being built.
     */
    protected Date lastModified() {
      return lastModified;
    }

    /**
     * Sets the content language of the {@link Representation} being built.
     *
     * @param language The desired content language fo the {@link Representation} being built.
     * @return The updated {@link Representation} builder.
     */
    public Builder language(Locale language) {
      this.language = language;
      return this;
    }

    /**
     * Retrieves the content language of the {@link Representation} being built.
     *
     * @return The content language of the {@link Representation} being built.
     */
    protected Locale language() {
      return this.language;
    }

    /**
     * Builds the {@link Representation} instance.
     *
     * @return The {@link Representation} instance.
     */
    public abstract Representation build();
  }

  /**
   * Retrieves the media type of the {@link Representation}.
   *
   * @return The media type of the {@link Representation}.
   */
  @JsonIgnore
  @XmlTransient
  public MediaType getMediaType() {
    return this.mediaType;
  }

  /**
   * Retrieves the canonical URI used to identify this representation.
   *
   * <p>See the <a href='https://tools.ietf.org/html/rfc7231#section-3.1.4.2'>Content-Location</a>
   * header as described in RFC7231.
   *
   * @return The canonical URI identifying this representation.
   */
  @JsonIgnore
  @XmlTransient
  public URI getLocation() {
    return this.location;
  }

  /**
   * Alters the canonical URI used to identify this representation.
   *
   * <p>See the <a href='https://tools.ietf.org/html/rfc7231#section-3.1.4.2'>Content-Location</a>
   * header as described in RFC7231. See the <a
   * href='https://tools.ietf.org/html/rfc7231#section-3.1.2.1'>Content codings</a> as described in
   * RFC7231.
   *
   * @param location The desired canonical URI identifying this representation.
   */
  @JsonIgnore
  public void setLocation(URI location) {
    this.location = location;
  }

  /**
   * Retrieves the coding scheme used for the representation.
   *
   * <p>See the <a href='https://tools.ietf.org/html/rfc7231#section-3.1.2.2'>Content-Encoding</a>
   * header as described in RFC7231. See the <a
   * href='https://tools.ietf.org/html/rfc7231#section-3.1.2.1'>Content codings</a> as described in
   * RFC7231.
   *
   * @return The coding scheme used to encode the representation.
   */
  @JsonIgnore
  @XmlTransient
  public String getEncoding() {
    return this.encoding;
  }

  /**
   * Alters the coding scheme that was used to encode used for the representation.
   *
   * <p>See the <a href='https://tools.ietf.org/html/rfc7231#section-3.1.2.2'>Content-Encoding</a>
   * header as described in RFC7231. See the <a
   * href='https://tools.ietf.org/html/rfc7231#section-3.1.2.1'>Content codings</a> as described in
   * RFC7231.
   *
   * @param encoding The desired coding scheme used to encode the representation.
   */
  @JsonIgnore
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  /**
   * Retrieves the language of the target audience of the representation.
   *
   * <p>See the <a href="https://tools.ietf.org/html/rfc7231#section-3.1.3.2">Content-Language</a>
   * header as discussed in RFC7231. See the <a
   * href='https://tools.ietf.org/html/rfc7231#section-3.1.3.1'>Language tags</a> as discussed in
   * RFC7231.
   *
   * @return The language of the target audience of the representation.
   */
  @JsonIgnore
  @XmlTransient
  public Locale getLanguage() {
    return this.language;
  }

  /**
   * Alters the language of the target audience of the representation.
   *
   * <p>The <a href='https://tools.ietf.org/html/rfc7231#section-3.1.3.2'>Content-Language</a>
   * header as discussed in RFC7231. See the <a
   * href='https://tools.ietf.org/html/rfc7231#section-3.1.3.1'>Language tags</a> as discussed in
   * RFC7231.
   *
   * @param language The desired language for the target audience of the representation.
   */
  @JsonIgnore
  public void setLanguage(Locale language) {
    this.language = language;
  }

  /**
   * Retrieves the last modified date and time of the representation. A representation may not have
   * a last modified date and time if the representation's metadata is not being tracked in a
   * persistent store.
   *
   * <p>See the <a href='https://tools.ietf.org/html/rfc7232#section-2.2'>Last-Modified</a> header
   * as discussed in RFC7231.
   *
   * @return The date and time that the representation was last modified if available; {@code null}
   *     otherwise.
   */
  @JsonIgnore
  @XmlTransient
  public Date getLastModified() {
    return this.lastModified;
  }

  /**
   * Alters the data and time that the this representation was last modified.
   *
   * <p>See the <a href='https://tools.ietf.org/html/rfc7232#section-2.2'>Last-Modified</a> header
   * as discussed in RFC7231.
   *
   * @param lastModified The desired date and time that the representation was last modified.
   */
  @JsonIgnore
  // @XmlTransient
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }
}
