package application;

import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;

public class NotificationFactory {

  private final TargetFactory targetFactory;
  private final AudienceFactory audienceFactory;

  @Inject
  public NotificationFactory(TargetFactory targetFactory, AudienceFactory audienceFactory) {
    this.targetFactory = targetFactory;
    this.audienceFactory = audienceFactory;
  }

  public Notification createFrom(api.representations.json.Notification notification) {
    Set<Target> targets = new HashSet<>();
    Set<Audience> audiences = new HashSet<>();

    for (api.representations.json.Target target : notification.getTargets()) {
      targets.add(this.targetFactory.createFrom(target));
    }

    for (api.representations.json.Audience audience : notification.getAudiences()) {
      audiences.add(this.audienceFactory.createFrom(audience));
    }

    return new Notification(
        notification.getUUID(),
        notification.getContent(),
        NotificationStatus.valueOf(notification.getStatus().toString()),
        targets,
        audiences,
        notification.getSendAt(),
        notification.getSentAt());
  }

  public Notification createFrom(api.representations.xml.Notification notification) {
    Set<Target> targets = new HashSet<>();
    Set<Audience> audiences = new HashSet<>();

    for (api.representations.xml.Target target : notification.getTargets()) {
      targets.add(this.targetFactory.createFrom(target));
    }

    for (api.representations.xml.Audience audience : notification.getAudiences()) {
      audiences.add(this.audienceFactory.createFrom(audience));
    }

    return new Notification(
        notification.getUUID(),
        notification.getContent(),
        NotificationStatus.valueOf(notification.getStatus().toString()),
        targets,
        audiences,
        notification.getSendAt(),
        notification.getSentAt());
  }

  public Notification createFrom(domain.Notification notification) {
    Set<Target> targets_sm = new HashSet<>();

    for (domain.Target target : notification.directRecipients()) {
      targets_sm.add(this.targetFactory.createFrom(target));
    }

    Set<Audience> audiences_sm = new HashSet<>();
    for (domain.Audience audience : notification.audiences()) {
      audiences_sm.add(this.audienceFactory.createFrom(audience));
    }

    Notification noti_sm =
        new application.Notification(
            notification.getId(),
            notification.content(),
            NotificationStatus.valueOf(notification.status().toString()),
            targets_sm,
            audiences_sm,
            notification.sendAt(),
            notification.sentAt());
    return noti_sm;
  }
}
