package api.representations.xml;

import api.representations.Representation;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "noti")
public final class Noti extends Representation {

  private Integer notificationCount;
  private Integer audienceCount;
  private Integer targetCount;

  public Noti() {
    super(MediaType.APPLICATION_JSON_TYPE);
  }

  public static final class Builder extends Representation.Builder {

    private int notificationCount;
    private int audienceCount;
    private int targetCount;

    public Builder() {
      super(MediaType.APPLICATION_JSON_TYPE);
    }

    public Builder notificationCount(int count) {
      this.notificationCount = count;
      return this;
    }

    public Builder audienceCount(int count) {
      this.audienceCount = count;
      return this;
    }

    public Builder targetCount(int count) {
      this.targetCount = count;
      return this;
    }

    @Override
    public Representation build() {
      Noti n = new Noti();
      n.setLocation(this.location());
      n.setEncoding(this.encoding());
      n.setLanguage(this.language());
      n.setLastModified(this.lastModified());
      n.setNotificationCount(this.notificationCount);
      n.setAudienceCount(this.audienceCount);
      n.setTargetCount(this.targetCount);
      return n;
    }
  }

  @XmlElement
  public Integer getNotificationCount() {
    return this.notificationCount;
  }

  private void setNotificationCount(Integer count) {
    this.notificationCount = count;
  }

  @XmlElement
  public Integer getAudienceCount() {
    return this.audienceCount;
  }

  private void setAudienceCount(Integer count) {
    this.audienceCount = count;
  }

  @XmlElement
  public Integer getTargetCount() {
    return this.targetCount;
  }

  private void setTargetCount(Integer count) {
    this.targetCount = count;
  }
}
