package api.representations.json;

import api.representations.Representation;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.ws.rs.core.MediaType;

/**
 * Defines the 'application/json' representation of a Notification resource.
 *
 * @author Jon Freer
 */
public final class Notification extends Representation {

  private UUID uuid;
  private String content;
  private Date sentAt;
  private Date sendAt;
  private NotificationStatus status;
  private Set<Target> targets;
  private Set<Audience> audiences;

  /** Constructs an empty instance of {@link Notification}. */
  private Notification() {
    super(MediaType.APPLICATION_JSON_TYPE);

    this.uuid = null;
    this.content = null;
    this.sentAt = null;
    this.sendAt = null;
    this.status = null;
    this.targets = new HashSet<>();
    this.audiences = new HashSet<>();
  }

  public static final class Builder extends Representation.Builder {

    private UUID uuid;
    private String content;
    private Date sentAt;
    private Date sendAt;
    private NotificationStatus status;
    private Set<Target> targets;
    private Set<Audience> audiences;

    public Builder() {
      super(MediaType.APPLICATION_JSON_TYPE);
      this.targets = new HashSet<>();
      this.audiences = new HashSet<>();
    }

    public Builder uuid(UUID uuid) {
      this.uuid = uuid;
      return this;
    }

    public Builder content(String content) {
      this.content = content;
      return this;
    }

    public Builder status(NotificationStatus status) {
      this.status = status;
      return this;
    }

    public Builder sentAt(Date sentAt) {
      this.sentAt = sentAt;
      return this;
    }

    public Builder sendAt(Date sendAt) {
      this.sendAt = sendAt;
      return this;
    }

    public Builder addTarget(Target target) {
      this.targets.add(target);
      return this;
    }

    public Builder targets(Set<Target> targets) {
      this.targets = targets;
      return this;
    }

    public Builder addAudience(Audience audience) {
      this.audiences.add(audience);
      return this;
    }

    public Builder audiences(Set<Audience> audiences) {
      this.audiences = audiences;
      return this;
    }

    @Override
    public Representation build() {
      Notification n = new Notification();
      n.setLocation(this.location());
      n.setEncoding(this.encoding());
      n.setLanguage(this.language());
      n.setLastModified(this.lastModified());
      n.setUUID(this.uuid);
      n.setContent(this.content);
      n.setSentAt(this.sentAt);
      n.setSendAt(this.sendAt);
      n.setTargets(this.targets);
      n.setAudiences(this.audiences);
      return n;
    }
  }

  /**
   * Retrieves the universally unique identifier of this notification.
   *
   * @return The universally unique identifier of this notification.
   */
  public UUID getUUID() {
    return this.uuid;
  }

  private void setUUID(UUID uuid) {
    this.uuid = uuid;
  }

  /**
   * Retrieves the information being communicated within this notification.
   *
   * @return The information being communicated within this notification.
   */
  public String getContent() {
    return this.content;
  }

  private void setContent(String content) {
    this.content = content;
  }

  /**
   * Retrieves the status the delivery status of this notification.
   *
   * @return The status of the notification in terms of its delivery to its audiences and targets.
   */
  public NotificationStatus getStatus() {
    return this.status;
  }

  private void setStatus(NotificationStatus status) {
    this.status = status;
  }

  /**
   * Retrieves the date and time in which this notification is to be sent. A value of {@code null}
   * indicates that the notification should be sent immediately.
   *
   * @return States when the notification should be sent to its targets and audiences.
   */
  public Date getSendAt() {
    return this.sendAt;
  }

  private void setSendAt(Date sendAt) {
    this.sendAt = sendAt;
  }

  /**
   * Retrieves the date and time in which this notification was sent to all of its targets and
   * audiences. A value of {@code null} indicates that the notification has not been sent to all of
   * its targets and audiences. Please refer to {@link api.representations.json.NotificationStatus}
   * to gain more insight into the progress of the notification delivery.
   *
   * @return States when the notification was sent to all of its targets and all of its audiences.
   */
  public Date getSentAt() {
    return this.sentAt;
  }

  private void setSentAt(Date sentAt) {
    this.sentAt = sentAt;
  }

  /**
   * Retrieves the explicit recipients that should receive this notification.
   *
   * @return Explicit recipients that should receive this notification.
   */
  public Set<Target> getTargets() {
    return this.targets;
  }

  private void setTargets(Set<Target> targets) {
    this.targets = targets;
  }

  /**
   * Retrieves the named audiences that should receive this notification.
   *
   * @return Broader audiences that should receive this notification.
   */
  public Set<Audience> getAudiences() {
    return this.audiences;
  }

  private void setAudiences(Set<Audience> audiences) {
    this.audiences = audiences;
  }
}
