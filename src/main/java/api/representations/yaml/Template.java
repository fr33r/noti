package api.representations.yaml;

import api.representations.Representation;
import com.fasterxml.jackson.jaxrs.yaml.YAMLMediaTypes;
import java.util.UUID;

/**
 * Defines the {@code application/yaml} representation of a Template resource.
 *
 * @author Jon Freer
 */
public final class Template extends Representation {

  private UUID uuid;
  private String content;

  /** Constructs a new {@link Template} representation. */
  private Template() {
    super(YAMLMediaTypes.APPLICATION_JACKSON_YAML_TYPE);
  }

  /**
   * A builder of {@link Template} instances.
   *
   * @author Jon Freer
   */
  public static final class Builder extends Representation.Builder {

    private UUID uuid;
    private String content;

    /** Constructs a builder of {@link Template} instances. */
    public Builder() {
      super(YAMLMediaTypes.APPLICATION_JACKSON_YAML_TYPE);
    }

    /**
     * Sets the universally unique identifier of the {@link Template} representation being built.
     *
     * @param uuid The desired {@link UUID} of the {@link Template} representation being built.
     * @return The updated {@link Template} builder.
     */
    public Builder uuid(UUID uuid) {
      this.uuid = uuid;
      return this;
    }

    /**
     * Sets the content of the {@link Template} representation being built.
     *
     * @param content The desired content of the {@link Template} representation being built.
     * @return The updated {@link Target} builder.
     */
    public Builder content(String content) {
      this.content = content;
      return this;
    }

    /**
     * Builds the {@link Template} instance.
     *
     * @return The {@link Template} instance.
     */
    @Override
    public Representation build() {
      Template a = new Template();
      a.setLocation(this.location());
      a.setEncoding(this.encoding());
      a.setLanguage(this.language());
      a.setUUID(this.uuid);
      a.setContent(this.content);
      return a;
    }
  }

  /**
   * Retrieves the universally unique identifier for this template representation.
   *
   * @return The universally unique identifier for this template representation.
   */
  public UUID getUUID() {
    return this.uuid;
  }

  /**
   * Alters the universally unique identifier of this template representation.
   *
   * @param uuid The desired universally unique identifier of the template representation.
   */
  private void setUUID(UUID uuid) {
    this.uuid = uuid;
  }

  /**
   * Retrieves the content of the template representation.
   *
   * @return The content of the template representation.
   */
  public String getContent() {
    return this.content;
  }

  /**
   * Alters the content of the template representation.
   *
   * @param content The desired content of the template representation.
   */
  private void setContent(String content) {
    this.content = content;
  }

  /**
   * {@inheritDoc}
   *
   * @param obj {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || this.getClass() != obj.getClass()) return false;

    Template template = (Template) obj;
    boolean sameUUID =
        this.getUUID() == null && template.getUUID() == null
            || this.getUUID() != null
                && template.getUUID() != null
                && this.getUUID().equals(template.getUUID());
    boolean sameContent =
        this.getContent() == null && template.getContent() == null
            || this.getContent() != null
                && template.getContent() != null
                && this.content.equals(template.getContent());

    return sameUUID && sameContent;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int hashCode = 1;
    final int prime = 17;

    if (this.getUUID() != null) {
      hashCode = hashCode * prime + this.getUUID().hashCode();
    }

    if (this.getContent() != null) {
      hashCode = hashCode * prime + this.getContent().hashCode();
    }

    return hashCode;
  }
}
