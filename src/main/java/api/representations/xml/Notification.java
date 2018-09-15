package api.representations.xml;

import api.representations.Representation;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Defines the {@code application/xml} representation of a Notification resource.
 *
 * @author Jon Freer
 */
@XmlRootElement(name = "notification")
public final class Notification extends Representation {

  private UUID uuid;
  private String content;
  private Date sentAt;
  private Date sendAt;
  private NotificationStatus status;
  private Set<Target> targets;
  private Set<Audience> audiences;
  private Set<Message> messages;

  /** Constructs a new {@link Notification} representation. */
  private Notification() {
    super(MediaType.APPLICATION_XML_TYPE);

    this.uuid = null;
    this.content = null;
    this.sentAt = null;
    this.sendAt = null;
    this.status = null;
    this.targets = new HashSet<>();
    this.audiences = new HashSet<>();
    this.messages = new HashSet<>();
  }

  /**
   * A builder of {@link Notification} instances.
   *
   * @author Jon Freer
   */
  public static final class Builder extends Representation.Builder {

    private UUID uuid;
    private String content;
    private Date sentAt;
    private Date sendAt;
    private NotificationStatus status;
    private Set<Target> targets;
    private Set<Audience> audiences;
    private Set<Message> messages;

    /** Constructs a builder of {@link Notification} instances. */
    public Builder() {
      super(MediaType.APPLICATION_XML_TYPE);
      this.targets = new HashSet<>();
      this.audiences = new HashSet<>();
      this.messages = new HashSet<>();
    }

    /**
     * Sets the universally unique identifier of the {@link Notification} representation being
     * built.
     *
     * @param uuid The desired {@link UUID} of the {@link Notification} representation being built.
     * @return The updated {@link Notification} builder.
     */
    public Builder uuid(UUID uuid) {
      this.uuid = uuid;
      return this;
    }

    /**
     * Sets the textual content of the {@link Notification} representation being built.
     *
     * @param content The desired textual content of the {@link Notification} representation being
     *     built.
     * @return The updated {@link Notification} builder.
     */
    public Builder content(String content) {
      this.content = content;
      return this;
    }

    /**
     * Sets the date and time of the {@link Notification} representation that indicates when the
     * notification was sent. A value of {@code null} indicates the notification has not yet been
     * sent.
     *
     * @param sentAt The desired date and time of the {@link Notification} representation indicating
     *     when the notification was sent.
     * @return The updated {@link Notification} builder.
     */
    public Builder sentAt(Date sentAt) {
      this.sentAt = sentAt;
      return this;
    }

    /**
     * Sets the date and time of the {@link Notification} representation that indicates when the
     * notification should be sent.
     *
     * @param sendAt The desired date and time of the {@link Notification} representation indicating
     *     when the notification should be sent.
     * @return The updated {@link Notification} builder.
     */
    public Builder sendAt(Date sendAt) {
      this.sendAt = sendAt;
      return this;
    }

    /**
     * Sets the status of the {@link Notification} representation being built.
     *
     * @param status The desired status of the {@link Notification} representation bing built.
     * @return The updated {@link Notification} builder.
     */
    public Builder status(NotificationStatus status) {
      this.status = status;
      return this;
    }

    /**
     * Appends the provided {@link Target} to the list of targets for the {@link Notification}
     * representation.
     *
     * @param target The {@link Target} to append to the list of targets for the {@link
     *     Notification} representation.
     * @return The updated {@link Notification} builder.
     */
    public Builder addTarget(Target target) {
      this.targets.add(target);
      return this;
    }

    /**
     * Sets the list of targets for the {@link Notification} representation.
     *
     * @param targets The desired list of targets for the {@link Notification} representation.
     * @return The updated {@link Notification} builder.
     */
    public Builder targets(Set<Target> targets) {
      this.targets = targets;
      return this;
    }

    /**
     * Appends the provided {@link Audience} to the list of audiences for the {@link Notification}
     * representation.
     *
     * @param audience The {@link Audience} to append to the list of audiences for the {@link
     *     Notification} representation.
     * @return The updated {@link Notification} builder.
     */
    public Builder addAudience(Audience audience) {
      this.audiences.add(audience);
      return this;
    }

    /**
     * Sets the list of audiences for the {@link Notification} representation.
     *
     * @param audiences The desired list of audiences for the {@link Notification} representation.
     * @return The updated {@link Notification} builder.
     */
    public Builder audiences(Set<Audience> audiences) {
      this.audiences = audiences;
      return this;
    }

    public Builder messages(Set<Message> messages) {
      this.messages = messages;
      return this;
    }

    /**
     * Builds the {@link Notification} instance.
     *
     * @return The {@link Notification} instance.
     */
    @Override
    public Representation build() {
      Notification n = new Notification();
      n.setLocation(this.location());
      n.setEncoding(this.encoding());
      n.setLanguage(this.language());
      n.setLastModified(this.lastModified());
      n.setUUID(this.uuid);
      n.setStatus(this.status);
      n.setContent(this.content);
      n.setSentAt(this.sentAt);
      n.setSendAt(this.sendAt);
      n.setTargets(this.targets);
      n.setAudiences(this.audiences);
      n.setMessages(this.messages);
      return n;
    }
  }

  /**
   * Retrieves the universally unique identifier of this notification representation.
   *
   * @return The universally unique identifier of this notification representation.
   */
  @XmlElement
  public UUID getUUID() {
    return this.uuid;
  }

  /**
   * Alters the universally unique identifier of this notification representation.
   *
   * @param uuid The desired universally unique identifier of the notification representation.
   */
  private void setUUID(UUID uuid) {
    this.uuid = uuid;
  }

  /**
   * Retrieves the textual information being communicated within this notification representation.
   *
   * @return The textual information being communicated within this notification representation.
   */
  @XmlElement
  public String getContent() {
    return this.content;
  }

  /**
   * Alters the information being communicated within this notification representation.
   *
   * @param content The desired information being communicated within this notification
   *     representation.
   */
  private void setContent(String content) {
    this.content = content;
  }

  /**
   * Retrieves the delivery status of this notification representation.
   *
   * @return The delivery status of the notification representation.
   */
  @XmlElement
  public NotificationStatus getStatus() {
    return this.status;
  }

  /**
   * Alters the delivery status of the notification representation.
   *
   * @param status The desired status of the notification representation.
   */
  private void setStatus(NotificationStatus status) {
    this.status = status;
  }

  /**
   * Retrieves the date and time of the notification representation indicating when the notification
   * should be sent. A value of {@code null} indicates that the notification should be sent
   * immediately.
   *
   * @return The date and time of the notification representation indicating when the notification
   *     should be sent.
   */
  @XmlElement
  public Date getSendAt() {
    return this.sendAt;
  }

  /**
   * Alters the date and time of the notification representation indicating when the notification
   * should be sent.
   *
   * @param sendAt The desired date and time of the notification representation indicating when the
   *     notification should be sent.
   */
  private void setSendAt(Date sendAt) {
    this.sendAt = sendAt;
  }

  /**
   * Retrieves the date and time of the notification representation indicating when the notification
   * was sent to all of its targets and audiences.
   *
   * @return The date and time of the notification representation indicating when the notification
   *     was sent to all of its targets and all of its audiences.
   */
  @XmlElement
  public Date getSentAt() {
    return this.sentAt;
  }

  /**
   * Alters the date and time of the notification representation indicating when the notification
   * should be sent.
   *
   * @param sendAt The desired date and time of the notification representation indicating when the
   *     notification should be sent.
   */
  private void setSentAt(Date sentAt) {
    this.sentAt = sentAt;
  }

  /**
   * Retrieves the list of targets of the notification representation that identify the explicit
   * individual recipients that should receive this notification.
   *
   * @return The list of targets of the notification representation that identify the explicit
   *     recipients that should receive this notification.
   */
  @XmlElementWrapper(name = "targets")
  @XmlElement(name = "target")
  public Set<Target> getTargets() {
    return this.targets;
  }

  /**
   * Alters the list of targets of the notification representation that identify the explicit
   * individual recipients that should receive this notification.
   *
   * @return The list of targets of the notification representation that identify the explicit
   *     recipients that should receive this notification.
   */
  private void setTargets(Set<Target> targets) {
    this.targets = targets;
  }

  /**
   * Retrieves the list of audiences of the notification representation indicating the named
   * audiences that should receive this notification.
   *
   * @return The list of audiences of the notification representation indicating the named audiences
   *     that should receive this notification.
   */
  @XmlElementWrapper(name = "audiences")
  @XmlElement(name = "audience")
  public Set<Audience> getAudiences() {
    return this.audiences;
  }

  /**
   * Alters the list of audiences of the notification representation indicating the named audiences
   * that should receive this notification.
   *
   * @param audiences The desired list of audiences of the notification representation indicating
   *     the named audiences that should receive this notification.
   */
  private void setAudiences(Set<Audience> audiences) {
    this.audiences = audiences;
  }

  @XmlElementWrapper(name = "messages")
  @XmlElement(name = "message")
  public Set<Message> getMessages() {
    return this.messages;
  }

  private void setMessages(Set<Message> messages) {
    this.messages = messages;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || obj.getClass() != this.getClass()) return false;
    Notification notification = (Notification) obj;

    boolean sameUUID =
        notification.getUUID() == null && this.getUUID() == null
            || notification.getUUID() != null
                && this.getUUID() != null
                && notification.getUUID().equals(this.getUUID());
    boolean sameContent =
        notification.getContent() == null && this.getContent() == null
            || notification.getContent() != null
                && this.getContent() != null
                && notification.getContent().equals(this.getContent());
    boolean sameStatus =
        notification.getStatus() == null && this.getStatus() == null
            || notification.getStatus() != null
                && this.getStatus() != null
                && notification.getStatus().equals(this.getStatus());
    boolean sameSendAt =
        notification.getSendAt() == null && this.getSendAt() == null
            || notification.getSendAt() != null
                && this.getSendAt() != null
                && notification.getSendAt().equals(this.getSendAt());
    boolean sameSentAt =
        notification.getSentAt() == null && this.getSentAt() == null
            || notification.getSentAt() != null
                && this.getSentAt() != null
                && notification.getSentAt().equals(this.getSentAt());
    boolean sameTargets =
        notification.getTargets() == null && this.getTargets() == null
            || notification.getTargets() != null
                && this.getTargets() != null
                && notification.getTargets().equals(this.getTargets());
    boolean sameAudiences =
        notification.getAudiences() == null && this.getAudiences() == null
            || notification.getAudiences() != null
                && this.getAudiences() != null
                && notification.getAudiences().equals(this.getAudiences());
    boolean sameMessages =
        notification.getMessages() == null && this.getMessages() == null
            || notification.getMessages() != null
                && this.getMessages() != null
                && notification.getMessages().equals(this.getMessages());

    return sameUUID
        && sameContent
        && sameStatus
        && sameSendAt
        && sameSentAt
        && sameTargets
        && sameAudiences
        && sameMessages;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    final int prime = 17;
    int hashCode = 1;

    if (this.getUUID() != null) {
      hashCode = hashCode * prime + this.getUUID().hashCode();
    }

    if (this.getContent() != null) {
      hashCode = hashCode * prime + this.getContent().hashCode();
    }

    if (this.getStatus() != null) {
      hashCode = hashCode * prime + this.getStatus().hashCode();
    }

    if (this.getSendAt() != null) {
      hashCode = hashCode * prime + this.getSendAt().hashCode();
    }

    if (this.getSentAt() != null) {
      hashCode = hashCode * prime + this.getSentAt().hashCode();
    }

    if (this.getTargets() != null) {
      hashCode = hashCode * prime + this.getTargets().hashCode();
    }

    if (this.getAudiences() != null) {
      hashCode = hashCode * prime + this.getAudiences().hashCode();
    }

    if (this.getMessages() != null) {
      hashCode = hashCode * prime + this.getMessages().hashCode();
    }

    return hashCode;
  }
}
