package api.representations.xml;

import api.representations.Representation;
import java.util.UUID;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "target")
public final class Target extends Representation {

  private UUID uuid;
  private String name;
  private String phoneNumber;

  private Target() {
    super(MediaType.APPLICATION_XML_TYPE);

    this.uuid = null;
    this.name = null;
    this.phoneNumber = null;
  }

  public static final class Builder extends Representation.Builder {
    private UUID uuid;
    private String name;
    private String phoneNumber;

    public Builder() {
      super(MediaType.APPLICATION_XML_TYPE);
    }

    public Builder uuid(UUID uuid) {
      this.uuid = uuid;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder phoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

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

  @XmlElement
  public UUID getUUID() {
    return uuid;
  }

  private void setUUID(UUID uuid) {
    this.uuid = uuid;
  }

  @XmlElement
  public String getName() {
    return name;
  }

  private void setName(String name) {
    this.name = name;
  }

  @XmlElement
  public String getPhoneNumber() {
    return phoneNumber;
  }

  private void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
}
