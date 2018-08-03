package domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NotificationBuilder extends EntityBuilder<Notification> {

  private UUID uuid;
  private String content;
  private Date sendAt;
  private Date sentAt;
  private Set<Target> targets;
  private Set<Message> messages;
  private Set<Audience> audiences;

  public NotificationBuilder content(String content) {
    this.content = content;
    return this;
  }

  public NotificationBuilder identity(UUID uuid) {
    this.uuid = uuid;
    return this;
  }

  public NotificationBuilder identity(String uuidString) {
    this.uuid = UUID.fromString(uuidString);
    return this;
  }

  public NotificationBuilder sendAt(Date sendAt) {
    this.sendAt = sendAt;
    return this;
  }

  public NotificationBuilder sentAt(Date sentAt) {
    this.sentAt = sentAt;
    return this;
  }

  public NotificationBuilder target(Target target) {
    if (this.targets == null) {
      this.targets = new HashSet<>();
    }
    this.targets.add(target);
    return this;
  }

  public NotificationBuilder targets(Target... targets) {
    for (Target target : targets) {
      this.target(target);
    }
    return this;
  }

  public NotificationBuilder targets(Set<Target> targets) {
    this.targets = targets;
    return this;
  }

  public NotificationBuilder message(Message message) {
    if (this.messages == null) {
      this.messages = new HashSet<>();
    }
    this.messages.add(message);
    return this;
  }

  public NotificationBuilder messages(Message... messages) {
    for (Message message : messages) {
      this.message(message);
    }
    return this;
  }

  public NotificationBuilder messages(Set<Message> messages) {
    this.messages = messages;
    return this;
  }

  public NotificationBuilder audience(Audience audience) {
    if (this.audiences == null) {
      this.audiences = new HashSet<>();
    }
    this.audiences.add(audience);
    return this;
  }

  public NotificationBuilder audiences(Audience... audiences) {
    for (Audience audience : audiences) {
      this.audience(audience);
    }
    return this;
  }

  public NotificationBuilder audiences(Set<Audience> audiences) {
    this.audiences = audiences;
    return this;
  }

  @Override
  public Notification build() {
    return new Notification(
        this.uuid,
        this.content,
        this.targets,
        this.messages,
        this.audiences,
        this.sendAt,
        this.sentAt);
  }
}
