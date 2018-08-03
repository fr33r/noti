package application;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Defines the general representation of a Notification resource. This representation can easily be
 * deserialized and serialized for the 'application/json' and 'application/xml' media types.
 *
 * @author Jon Freer
 */
public class Notification {

  private UUID uuid;
  private String content;
  private Date sentAt;
  private Date sendAt;
  private NotificationStatus status;
  private Set<Target> targets;
  private Set<Audience> audiences;

  /** Constructs an empty instance of {@link Notification}. */
  public Notification() {
    this.uuid = null;
    this.content = null;
    this.sentAt = null;
    this.sendAt = null;
    this.status = null;
    this.targets = new HashSet<>();
    this.audiences = new HashSet<>();
  }

  /**
   * Constructs a fully initialized instances of {@link Notification}.
   *
   * @param uuid The universally unique identifier of the Notification resource.
   * @param content The information being communicated within the notification.
   * @param status The status of the notification in terms of its delivery to its audiences and
   *     targets.
   * @param targets Explicit recipients that should receive this notification.
   * @param audiences Broader audiences that should receive this notification.
   * @param sendAt States when the notification should be sent to its targets and audiences.
   * @param sentAt States when the notification was sent to all of its targets and all of its
   *     audiences.
   */
  public Notification(
      UUID uuid,
      String content,
      NotificationStatus status,
      Set<Target> targets,
      Set<Audience> audiences,
      Date sendAt,
      Date sentAt) {
    this.uuid = uuid;
    this.content = content;
    this.status = status;
    this.sendAt = sendAt;
    this.sentAt = sentAt;
    this.targets = targets == null ? new HashSet<>() : targets;
    this.audiences = audiences == null ? new HashSet<>() : audiences;
  }

  /**
   * Retrieves the universally unique identifier of this notification.
   *
   * @return The universally unique identifier of this notification.
   */
  public UUID getUUID() {
    return this.uuid;
  }

  /**
   * Retrieves the information being communicated within this notification.
   *
   * @return The information being communicated within this notification.
   */
  public String getContent() {
    return this.content;
  }

  /**
   * Retrieves the status the delivery status of this notification.
   *
   * @return The status of the notification in terms of its delivery to its audiences and targets.
   */
  public NotificationStatus getStatus() {
    return this.status;
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

  /**
   * Retrieves the date and time in which this notification was sent to all of its targets and
   * audiences. A value of {@code null} indicates that the notification has not been sent to all of
   * its targets and audiences. Please refer to {@link application.NotificationStatus} to gain more
   * insight into the progress of the notification delivery.
   *
   * @return States when the notification was sent to all of its targets and all of its audiences.
   */
  public Date getSentAt() {
    return this.sentAt;
  }

  /**
   * Retrieves the explicit recipients that should receive this notification.
   *
   * @return Explicit recipients that should receive this notification.
   */
  public Set<Target> getTargets() {
    return this.targets;
  }

  /**
   * Retrieves the named audiences that should receive this notification.
   *
   * @return Broader audiences that should receive this notification.
   */
  public Set<Audience> getAudiences() {
    return this.audiences;
  }
}
