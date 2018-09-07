package api.representations.xml;

import api.representations.Representation;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "notificationCollection")
public final class NotificationCollection extends Representation {

  private int total;
  private Set<Representation> notifications;

  private NotificationCollection() {
    super(MediaType.APPLICATION_XML_TYPE);
    this.notifications = new HashSet<>();
    this.total = 0;
  }

  public static final class Builder extends Representation.Builder {

    private int total;
    private Set<Representation> notifications;

    public Builder() {
      super(MediaType.APPLICATION_XML_TYPE);
      this.notifications = new HashSet<>();
      this.total = 0;
    }

    public Builder addNotification(Representation notification) {
      this.notifications.add(notification);
      return this;
    }

    public Builder total(int total) {
      this.total = total;
      return this;
    }

    @Override
    public Representation build() {
      NotificationCollection nc = new NotificationCollection();
      nc.setNotifications(this.notifications);
      nc.setTotal(this.total);
      return nc;
    }
  }

  private void setNotifications(Set<Representation> notifications) {
    this.notifications = notifications;
  }

  @XmlElementWrapper(name = "notifications")
  @XmlElement(name = "notification")
  public Set<Representation> getNotifications() {
    return this.notifications;
  }

  private void setTotal(int total) {
    this.total = total;
  }

  @XmlElement
  public int getTotal() {
    return this.total;
  }
}
