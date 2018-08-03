package domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.inject.Named;

@Named("NotificationFactory")
public class NotificationFactory {

  // i hate this. can't create mappers as they violate DDD. mappers are factories.
  public Notification createFrom(application.Notification notification) {
    Notification noti = this.create(notification);
    Set<Message> messages = this.createMessages(noti);
    noti.messages(messages);
    return noti;
  }

  public Notification createFrom(application.Notification notification, Set<Message> messages) {
    Notification noti = this.create(notification);
    noti.messages(messages);
    return noti;
  }

  private Set<Message> createMessages(Notification notification) {
    Set<Message> messages = new HashSet<>();
    final PhoneNumber from = new PhoneNumber("1", "614", "412", "5540");
    int sequenceNum = 0;

    for (Target target : notification.directRecipients()) {
      messages.add(
          new Message(
              sequenceNum,
              from,
              target.getPhoneNumber(),
              notification.content(),
              MessageStatus.PENDING,
              ""));
      sequenceNum++;
    }

    for (Audience audience : notification.audiences()) {
      for (Target member : audience.members()) {
        boolean alreadyExists = false;
        for (Message message : messages) {
          if (message.getTo().toE164().equals(member.getPhoneNumber().toE164())) {
            alreadyExists = true;
            break;
          }
        }

        if (!alreadyExists) {
          messages.add(
              new Message(
                  sequenceNum,
                  from,
                  member.getPhoneNumber(),
                  notification.content(),
                  MessageStatus.PENDING,
                  ""));
          sequenceNum++;
        }
      }
    }

    return messages;
  }

  private Notification create(application.Notification notification) {
    Set<Target> targets = new HashSet<>();
    for (application.Target target : notification.getTargets()) {
      targets.add(
          new Target(target.getUUID(), target.getName(), new PhoneNumber(target.getPhoneNumber())));
    }
    Set<Audience> audiences = new HashSet<>();
    for (application.Audience audience : notification.getAudiences()) {
      audiences.add(new Audience(audience.getUUID(), audience.getName(), new HashSet<>()));
    }

    NotificationBuilder builder = new NotificationBuilder();
    UUID uuid = notification.getUUID() == null ? UUID.randomUUID() : notification.getUUID();

    Notification domainNotification =
        builder
            .identity(uuid)
            .content(notification.getContent())
            .targets(targets)
            .audiences(audiences)
            .sendAt(notification.getSendAt())
            .sentAt(notification.getSentAt())
            .build();

    // compare the persisted state with the state computed:
    NotificationStatus desiredStatus =
        NotificationStatus.valueOf(notification.getStatus().toString());
    if (domainNotification.status() != desiredStatus) {
      // throw.
    }

    return domainNotification;
  }
}
