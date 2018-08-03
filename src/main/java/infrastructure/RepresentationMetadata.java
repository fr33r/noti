package infrastructure;

import java.net.URI;
import java.util.Date;
import java.util.Locale;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;

/**
 * Represents metadata about a single REST resource.
 *
 * @author jonfreer
 * @since 03/26/2017
 */
public class RepresentationMetadata {

  private final URI contentLocation;
  private final MediaType contentType;
  private final Locale contentLanguage;
  private final String contentEncoding;
  private final Date lastModified;
  private final EntityTag entityTag;

  public RepresentationMetadata(
      URI contentLocation,
      MediaType contentType,
      Locale contentLanguage,
      String contentEncoding,
      Date lastModified,
      EntityTag entityTag) {

    if (contentLocation == null) {
      throw new IllegalArgumentException(
          "The constructor argument 'contentLocation' cannot be null.");
    }

    if (contentType == null) {
      throw new IllegalArgumentException("The constructor argument 'contentType' cannot be null.");
    }

    if (lastModified == null) {
      throw new IllegalArgumentException("The constructor argument 'lastModified' cannot be null.");
    }

    if (entityTag == null) {
      throw new IllegalArgumentException("The constructor argument 'entityTag' cannot be null.");
    }

    this.contentLocation = contentLocation;
    this.contentType = contentType;
    this.lastModified = (Date) lastModified.clone();
    this.entityTag = entityTag;
    this.contentLanguage = contentLanguage;
    this.contentEncoding = contentEncoding;
  }

  /**
   * Retrieves the URI identifying the resource.
   *
   * @return The URI identifying the resource.
   */
  public URI getContentLocation() {
    return this.contentLocation;
  }

  /**
   * Retrieves the content type of the resource representation.
   *
   * @return The date and time that the resource was last modified.
   */
  public MediaType getContentType() {
    return this.contentType;
  }

  public Locale getContentLanguage() {
    return this.contentLanguage;
  }

  public String getContentEncoding() {
    return this.contentEncoding;
  }

  /**
   * Retrieves the date and time that the resource was last modified.
   *
   * @return The date and time that the resource was last modified.
   */
  public Date getLastModified() {
    return this.lastModified == null ? null : (Date) this.lastModified.clone();
  }

  /**
   * Retrieves the entity tag (also referred to as an ETag) for the resource representation.
   *
   * @return The entity tag for the resource.
   */
  public EntityTag getEntityTag() {
    return this.entityTag;
  }

  /**
   * {@inheritDoc}
   *
   * @param obj {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }

    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    RepresentationMetadata other = (RepresentationMetadata) obj;
    boolean hasSameEntityTag = this.entityTag.equals(other.entityTag);
    boolean hasSameContentType = this.contentType.equals(other.contentType);
    boolean hasSameLastModified = this.lastModified.equals(other.lastModified);
    boolean hasSameContentLocation = this.contentLocation.equals(other.contentLocation);
    boolean hasSameContentLanguage = this.contentLanguage.equals(other.contentLanguage);
    boolean hasSameContentEncoding = this.contentEncoding.equals(other.contentEncoding);

    return hasSameEntityTag
        && hasSameContentType
        && hasSameLastModified
        && hasSameContentLocation
        && hasSameContentLanguage
        && hasSameContentEncoding;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.entityTag.hashCode();
    result = prime * result + this.contentType.hashCode();
    result = prime * result + this.lastModified.hashCode();
    result = prime * result + this.contentLocation.hashCode();
    result = prime * result + this.contentLanguage.hashCode();
    result = prime * result + this.contentEncoding.hashCode();
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RepresentationMetadata [contentLocation=");
    builder.append(contentLocation);
    builder.append(", contentType=");
    builder.append(contentType);
    builder.append(", contentLanguage=");
    builder.append(contentLanguage);
    builder.append(", contentEncoding=");
    builder.append(contentEncoding);
    builder.append(", lastModified=");
    builder.append(lastModified);
    builder.append(", entityTag=");
    builder.append(entityTag);
    builder.append("]");
    return builder.toString();
  }
}
