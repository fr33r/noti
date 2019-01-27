package domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** */
public class Notification extends Entity<UUID> {

  private String content;
  private NotificationStatus status;
  private Date sendAt;
  private Date sentAt;
  private Set<Target> targets;
  private Set<Message> messages;
  private NotificationState state;
  private Set<Audience> audiences;

  Notification(
      UUID uuid,
      String content,
      Set<Target> targets,
      Set<Message> messages,
      Set<Audience> audiences,
      Date sendAt,
      Date sentAt) {
    super(uuid);
    this.setState(new PendingState());
    this.status = NotificationStatus.PENDING;
    this.messages(messages);
    this.content(content);
    this.sendAt(sendAt);
    this.sentAt(sentAt);
    this.directRecipients(targets);
    this.audiences(audiences);
  }

  public Notification(Notification notification) {
    this.setState(notification.state());
    this.messages(notification.messages());
    this.content(notification.content());
    this.sendAt(notification.sendAt());
    this.sentAt(notification.sentAt());
    this.directRecipients(notification.directRecipients());
    this.audiences(notification.audiences());
  }

  @Override
  public boolean isAggregateRoot() {
    return true;
  }

  public Set<Audience> audiences() {
    return this.audiences;
  }

  void setState(NotificationState state) {
    this.state = state;
  }

  NotificationState state() {
    return this.state;
  }

  public Set<Message> messages() {
    return messages;
  }

  public String content() {
    return this.content;
  }

  public Set<Target> directRecipients() {
    Set<Target> targetsCopy = new HashSet<>();
    for (Target target : this.targets) {
      targetsCopy.add((Target) target.clone());
    }
    return targetsCopy;
  }

  public void includeRecipient(Target target) {
    this.targets.add(target);
  }

  public NotificationStatus status() {
    return this.status;
  }

  void status(NotificationStatus status) {
    this.status = status;
  }

  public Date sendAt() {
    if (this.sendAt == null) {
      return null;
    }
    return (Date) this.sendAt.clone();
  }

  public Date sentAt() {
    if (this.sentAt == null) {
      return null;
    }
    return (Date) this.sentAt.clone();
  }

  protected void sentAt(Date sentAt) {
    this.sentAt = sentAt;
    this.state().next(this);
  }

  private void content(String content) {
    this.content = content;
  }

  private void sendAt(Date sendAt) {
    this.sendAt = sendAt;
    this.state().next(this);
  }

  public void directRecipients(Set<Target> targets) {
    if (targets == null) {
      this.targets = new HashSet<>();
    } else {
      this.targets = targets;
    }
  }

  public void audiences(Set<Audience> audiences) {
    if (audiences == null) {
      this.audiences = new HashSet<>();
    } else {
      this.audiences = audiences;
    }
  }

  public void messages(Set<Message> messages) {
    if (messages == null) {
      this.messages = new HashSet<>();
    } else {
      this.messages = messages;
    }
    this.state().next(this);
  }

  public boolean containsMessage(Message message) {
    if (message == null) {
      throw new IllegalArgumentException("The argument 'message' cannot be null.");
    }

    if (this.messages == null || this.messages.size() == 0) {
      return false;
    }

    return this.messages.contains(message);
  }

  public boolean containsMessage(Integer messageId) {
    if (messageId == null) {
      throw new IllegalArgumentException("The argument 'messageId' cannot be null.");
    }

    if (this.messages == null || this.messages.size() == 0) {
      return false;
    }

    for (Message message : this.messages) {
      if (message.getId() == messageId) {
        return true;
      }
    }

    return false;
  }

  public Message message(Integer messageId) {
    Message desiredMessage = this._message(messageId);
    return new Message(desiredMessage);
  }

  private Message _message(Integer messageId) {
    if (messageId == null) {
      throw new IllegalArgumentException("The argument 'messageId' cannot be null.");
    }

    Message desiredMessage = null;
    if (this.messages == null || this.messages.size() == 0) {
      return desiredMessage;
    }

    for (Message message : this.messages) {
      if (message.getId() == messageId) {
        desiredMessage = message;
        break;
      }
    }

    return desiredMessage;
  }

  public void messageExternalID(Integer messageID, String externalID) {
    if (this.containsMessage(messageID)) {
      Message message = this._message(messageID);
      message.setExternalId(externalID);
      this.state().next(this);
    }
  }

  public void messageStatus(Integer messageID, MessageStatus status) {
    if (this.containsMessage(messageID)) {
      Message message = this._message(messageID);
      message.setStatus(status);
      this.state().next(this);
    }
  }

  public void message(Message message) {
    if (this.containsMessage(message.getId())) {
      Message _message = this._message(message.getId());
      _message.setExternalId(message.getExternalId());
      _message.setStatus(message.getStatus());
      this.state().next(this);
    }
  }
}
