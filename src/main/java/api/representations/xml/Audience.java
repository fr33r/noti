package api.representations.xml;

import api.representations.Representation;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Defines the {@code application/xml} representation of an Audience resource.
 *
 * @author Jon Freer
 */
@XmlRootElement(name = "audience")
public final class Audience extends Representation {

  private UUID uuid;
  private String name;
  private Set<Target> members;

  /** Constructs a new {@link Audience} representation. */
  private Audience() {
    super(MediaType.APPLICATION_XML_TYPE);

    this.uuid = null;
    this.name = null;
    this.members = new HashSet<>();
  }

  /**
   * A builder of {@link Audience} instances.
   *
   * @author Jon Freer
   */
  public static final class Builder extends Representation.Builder {

    private UUID uuid;
    private String name;
    private Set<Target> members;

    /** Constructs a builder of {@link Audience} instances. */
    public Builder() {
      super(MediaType.APPLICATION_XML_TYPE);
      this.members = new HashSet<>();
    }

    /**
     * Sets the universally unique identifier of the {@link Audience} representation being built.
     *
     * @param uuid The desired {@link UUID} of the {@link Audience} representation being built.
     * @return The updated {@link Audience} builder.
     */
    public Builder uuid(UUID uuid) {
      this.uuid = uuid;
      return this;
    }

    /**
     * Sets the name of the {@link Audience} representation being built.
     *
     * @param name The desired name of the {@link Audience} representation being built.
     * @return The updated {@link Target} builder.
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Appends the provided {@link Target} to the list of members for the {@link Audience}
     * representation.
     *
     * @param member The {@link Target} to append to the list of members for the {@link Audience}
     *     representation.
     * @return The updated {@link Audience} builder.
     */
    public Builder addMember(Target member) {
      this.members.add(member);
      return this;
    }

    /**
     * Sets the list of members for the {@link Audience} representation.
     *
     * @param members The desired list of members for the {@link Audience} representation.
     * @return The updated {@link Audience} builder.
     */
    public Builder members(Set<Target> members) {
      this.members = members;
      return this;
    }

    /**
     * Builds the {@link Audience} instance.
     *
     * @return The {@link Audience} instance.
     */
    @Override
    public Representation build() {
      Audience a = new Audience();
      a.setLocation(this.location());
      a.setEncoding(this.encoding());
      a.setLanguage(this.language());
      a.setLastModified(this.lastModified());
      a.setUUID(this.uuid);
      a.setName(this.name);
      a.setMembers(this.members);
      return a;
    }
  }

  /**
   * Retrieves the universally unique identifier for this audience representation.
   *
   * @return The universally unique identifier for this audience representation.
   */
  @XmlElement
  public UUID getUUID() {
    return this.uuid;
  }

  /**
   * Alters the universally unique identifier of this audience representation.
   *
   * @param uuid The desired universally unique identifier of the audience representation.
   */
  private void setUUID(UUID uuid) {
    this.uuid = uuid;
  }

  /**
   * Retrieves the name of the audience representation.
   *
   * @return The name of the audience representation.
   */
  @XmlElement
  public String getName() {
    return this.name;
  }

  /**
   * Alters the name of the audience representation.
   *
   * @param name The desired name of the audience representation.
   */
  private void setName(String name) {
    this.name = name;
  }

  /**
   * Retrieves the members of the audience representation.
   *
   * @return The members of the audience representation.
   */
  @XmlElementWrapper(name = "members")
  @XmlElement(name = "member")
  public Set<Target> getMembers() {
    return this.members;
  }

  /**
   * Alters the list of members of the audience representation that identify the individual members
   * within the audience.
   *
   * @return The list of members of the audience representation that identify the individual members
   *     within the audience.
   */
  private void setMembers(Set<Target> members) {
    this.members = members;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || this.getClass() != obj.getClass()) return false;

    Audience audience = (Audience) obj;
    boolean sameUUID =
        this.getUUID() == null && audience.getUUID() == null
            || this.getUUID() != null
                && audience.getUUID() != null
                && this.getUUID().equals(audience.getUUID());
    boolean sameName =
        this.getName() == null && audience.getName() == null
            || this.getName() != null
                && audience.getName() != null
                && this.name.equals(audience.getName());
    boolean sameMembers =
        this.getMembers() == null && audience.getMembers() == null
            || this.getMembers() != null
                && audience.getMembers() != null
                && this.getMembers().equals(audience.getMembers());

    return sameUUID && sameName && sameMembers;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    int hashCode = 1;
    final int prime = 17;

    if (this.getUUID() != null) {
      hashCode = hashCode * prime + this.getUUID().hashCode();
    }

    if (this.getName() != null) {
      hashCode = hashCode * prime + this.getName().hashCode();
    }

    if (this.getMembers() != null) {
      hashCode = hashCode * prime + this.getMembers().hashCode();
    }

    return hashCode;
  }
}
