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
 * Defines the 'application/xml' representation of an Audience resource.
 *
 * @author Jon Freer
 */
@XmlRootElement(name = "audience")
public final class Audience extends Representation {

  private UUID uuid;
  private String name;
  private Set<Target> members;

  /** Constructs an empty instance of {@link Audience}. */
  private Audience() {
    super(MediaType.APPLICATION_XML_TYPE);

    this.uuid = null;
    this.name = null;
    this.members = new HashSet<>();
  }

  public static final class Builder extends Representation.Builder {

    private UUID uuid;
    private String name;
    private Set<Target> members;

    public Builder() {
      super(MediaType.APPLICATION_XML_TYPE);
      this.members = new HashSet<>();
    }

    public Builder uuid(UUID uuid) {
      this.uuid = uuid;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder addMember(Target member) {
      this.members.add(member);
      return this;
    }

    public Builder members(Set<Target> members) {
      this.members = members;
      return this;
    }

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
   * Retrieves the universally unique identifier for this audience.
   *
   * @return The universally unique identifier for this audience.
   */
  @XmlElement
  public UUID getUUID() {
    return this.uuid;
  }

  private void setUUID(UUID uuid) {
    this.uuid = uuid;
  }

  /**
   * Retrieves the name of the audience.
   *
   * @return The name of the audience.
   */
  @XmlElement
  public String getName() {
    return this.name;
  }

  private void setName(String name) {
    this.name = name;
  }

  /**
   * Retrieves the members that collectively resemble this audience.
   *
   * @return The members that collectively resemble this audience.
   */
  @XmlElementWrapper(name = "members")
  @XmlElement(name = "member")
  public Set<Target> getMembers() {
    return this.members;
  }

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
