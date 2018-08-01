package api.representations.xml;

import api.representations.Representation;
import java.util.UUID;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Defines the {@code application/json} representation of a Target resource.
 *
 * @author Jon Freer
 */
@XmlRootElement(name = "target")
public final class Target extends Representation {

  private UUID uuid;
  private String name;
  private String phoneNumber;

  /** Constructs a new {@link Target} representation. */
  private Target() {
    super(MediaType.APPLICATION_XML_TYPE);

    this.uuid = null;
    this.name = null;
    this.phoneNumber = null;
  }

  /**
   * A builder of {@link Target} instances.
   *
   * @author Jon Freer
   */
  public static final class Builder extends Representation.Builder {
    private UUID uuid;
    private String name;
    private String phoneNumber;

    /** Constructs a builder of {@link Target} instances. */
    public Builder() {
      super(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Sets the universally unique identifier of the {@link Target} representation being built.
     *
     * @param uuid The desired {@link UUID} of the {@link Target} representation being built.
     * @return The updated {@link Target} builder.
     */
    public Builder uuid(UUID uuid) {
      this.uuid = uuid;
      return this;
    }

    /**
     * Sets the name of the {@link Target} representation being built.
     *
     * @param name The desired name of the {@link Target} representation being built.
     * @return The updated {@link Target} builder.
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets the phone number of the {@link Target} representation being built.
     *
     * @param phoneNumber The desired phone number of the {@link Target} representation being built.
     * @return The updated {@link Target} builder.
     */
    public Builder phoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    /**
     * Builds the {@link Target} instance.
     *
     * @return The {@link Target} instance.
     */
    @Override
    public Representation build() {
      Target t = new Target();
      t.setLocation(this.location());
      t.setEncoding(this.encoding());
      t.setLanguage(this.language());
      t.setLastModified(this.lastModified());
      t.setUUID(this.uuid);
      t.setName(this.name);
      t.setPhoneNumber(this.phoneNumber);
      return t;
    }
  }

  /**
   * Retrieves the universally unique identifier of this target representation.
   *
   * @return The universally unique identifier of this target representation.
   */
  @XmlElement
  public UUID getUUID() {
    return uuid;
  }

  /**
   * Alters the universally unique identifier of this target representation.
   *
   * @param uuid The desired universally unique identifier of the target representation.
   */
  private void setUUID(UUID uuid) {
    this.uuid = uuid;
  }

  /**
   * Retrieves the name of the target representation.
   *
   * @return The name of the target representation.
   */
  @XmlElement
  public String getName() {
    return name;
  }

  /**
   * Alters the name of the target representation.
   *
   * @param name The desired name of the target representation.
   */
  private void setName(String name) {
    this.name = name;
  }

  /**
   * Retrieves the phone number of the target representation.
   *
   * @return The phone number of the target representation.
   */
  @XmlElement
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * Alters the phone number of the target representation.
   *
   * @param phoneNumber The desired phone number of the target representation.
   */
  private void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || obj.getClass() != this.getClass()) return false;

    Target target = (Target) obj;

    boolean sameUUID =
        this.getUUID() == null && target.getUUID() == null
            || this.getUUID() != null
                && target.getUUID() != null
                && this.getUUID().equals(target.getUUID());
    boolean sameName =
        this.getName() == null && target.getName() == null
            || this.getName() != null
                && target.getName() != null
                && this.getName().equals(target.getName());
    boolean samePhoneNumber =
        this.getPhoneNumber() == null && target.getPhoneNumber() == null
            || this.getPhoneNumber() != null
                && target.getPhoneNumber() != null
                && this.getPhoneNumber().equals(target.getPhoneNumber());

    return sameUUID && sameName && samePhoneNumber;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    final int prime = 17;
    int hashCode = 1;

    if (this.getUUID() != null) {
      hashCode = hashCode * prime + this.getUUID().hashCode();
    }

    if (this.getName() != null) {
      hashCode = hashCode * prime + this.getName().hashCode();
    }

    if (this.getPhoneNumber() != null) {
      hashCode = hashCode * prime + this.getPhoneNumber().hashCode();
    }

    return hashCode;
  }
}
