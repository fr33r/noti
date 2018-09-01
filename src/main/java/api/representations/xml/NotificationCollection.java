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

  private Set<Representation> notifications;

  private NotificationCollection() {
    super(MediaType.APPLICATION_XML_TYPE);
    this.notifications = new HashSet<>();
  }

  public static final class Builder extends Representation.Builder {

    private Set<Representation> notifications;

    public Builder() {
      super(MediaType.APPLICATION_XML_TYPE);
      this.notifications = new HashSet<>();
    }

    public Builder addNotification(Representation notification) {
      this.notifications.add(notification);
      return this;
    }

    @Override
    public Representation build() {
      NotificationCollection nc = new NotificationCollection();
      nc.setNotifications(this.notifications);
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
}
