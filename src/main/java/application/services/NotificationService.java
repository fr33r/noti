package application.services;

import domain.Message;
import domain.MessageFactory;
import domain.Notification;
import domain.NotificationFactory;
import infrastructure.MessageMetadata;
import infrastructure.MessageQueueService;
import infrastructure.Query;
import infrastructure.QueryFactory;
import infrastructure.Repository;
import infrastructure.RepositoryFactory;
import infrastructure.SQLUnitOfWork;
import infrastructure.SQLUnitOfWorkFactory;
import io.opentracing.Tracer;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import javax.inject.Inject;

public final class NotificationService implements application.NotificationService {

  private final SQLUnitOfWorkFactory unitOfWorkFactory;
  private final RepositoryFactory repositoryFactory;
  private final NotificationFactory notificationFactory;
  private final application.NotificationFactory applicationNotificationFactory;
  private final MessageFactory messageFactory;
  private final application.MessageFactory applicationMessageFactory;
  private final MessageQueueService smsQueueService;
  private final QueryFactory<domain.Notification> queryFactory;
  private final Tracer tracer;

  @Inject
  public NotificationService(
      SQLUnitOfWorkFactory unitOfWorkFactory,
      RepositoryFactory repositoryFactory,
      MessageQueueService smsQueueService,
      NotificationFactory notificationFactory,
      application.NotificationFactory applicationNotificationFactory,
      QueryFactory<domain.Notification> queryFactory,
      MessageFactory messageFactory,
      application.MessageFactory applicationMessageFactory,
      Tracer tracer) {
    this.unitOfWorkFactory = unitOfWorkFactory;
    this.repositoryFactory = repositoryFactory;
    this.smsQueueService = smsQueueService;
    this.notificationFactory = notificationFactory;
    this.messageFactory = messageFactory;
    this.applicationMessageFactory = applicationMessageFactory;
    this.applicationNotificationFactory = applicationNotificationFactory;
    this.queryFactory = queryFactory;
    this.tracer = tracer;
  }

  public Set<application.Notification> getNotifications(
      String externalMessageID, Integer skip, Integer take) {
    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
    try {
      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);

      Query<domain.Notification> query = this.queryFactory.createQuery(unitOfWork);

      if (externalMessageID != null) {
        query.add(
            query.equalTo(
                query.field(MessageMetadata.EXTERNAL_ID), query.string(externalMessageID)));
      }

      // TODO - should find a way so that the order of which
      // these method calls on query does not influence outcome.
      if (take != null) {
        query.limit(take);
      }

      if (skip != null) {
        query.skip(skip);
      }

      Set<domain.Notification> notifications_dm = notificationRepository.get(query);

      Set<application.Notification> notifications = new HashSet<>();

      for (domain.Notification notification : notifications_dm) {
        notifications.add(this.applicationNotificationFactory.createFrom(notification));
      }

      return notifications;
    } catch (Exception x) {
      unitOfWork.undo();
      throw new RuntimeException(x);
    }
  }

  public Integer getNotificationCount() {

    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
    try {
      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);
      return notificationRepository.size();
    } catch (Exception x) {
      unitOfWork.undo();
      throw new RuntimeException(x);
    }
  }

  public Set<application.Target> getNotificationDirectRecipients(
      UUID uuid, Integer skip, Integer take) {
    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
    try {
      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);
      domain.Notification notification = notificationRepository.get(uuid);
      if (notification == null) {
        throw new RuntimeException(
            String.format("Can't find notification with UUID of '%s'", uuid.toString()));
      }
      application.Notification noti = this.applicationNotificationFactory.createFrom(notification);
      Set<application.Target> targets = noti.getTargets();
      Set<application.Target> filteredTargets = new HashSet<>();
      application.Target[] targetArray = new application.Target[targets.size()];
      targets.toArray(targetArray);
      for (int idx = 0; idx < targets.size(); idx++) {
        if (skip != null && idx <= skip - 1) {
          continue;
        }
        if (take != null && filteredTargets.size() >= take) {
          break;
        }
        filteredTargets.add(targetArray[idx]);
      }
      return filteredTargets;
    } catch (Exception x) {
      unitOfWork.undo();
      throw new RuntimeException(x);
    }
  }

  public Set<application.Audience> getNotificationAudiences(UUID uuid, Integer skip, Integer take) {
    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
    try {
      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);
      domain.Notification notification = notificationRepository.get(uuid);
      if (notification == null) {
        throw new RuntimeException(
            String.format("Can't find notification with UUID of '%s'", uuid.toString()));
      }
      application.Notification noti = this.applicationNotificationFactory.createFrom(notification);
      Set<application.Audience> audiences = noti.getAudiences();
      Set<application.Audience> filteredAudiences = new HashSet<>();
      application.Audience[] audienceArray = new application.Audience[audiences.size()];
      audiences.toArray(audienceArray);
      for (int idx = 0; idx < audiences.size(); idx++) {
        if (skip != null && idx <= skip - 1) {
          continue;
        }
        if (take != null && filteredAudiences.size() >= take) {
          break;
        }
        filteredAudiences.add(audienceArray[idx]);
      }
      return filteredAudiences;
    } catch (Exception x) {
      unitOfWork.undo();
      throw new RuntimeException(x);
    }
  }

  public Set<application.Message> getNotificationMessages(UUID uuid, Integer skip, Integer take) {
    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
    try {
      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);
      domain.Notification notification = notificationRepository.get(uuid);
      if (notification == null) {
        throw new RuntimeException(
            String.format("Can't find notification with UUID of '%s'", uuid.toString()));
      }
      application.Notification noti = this.applicationNotificationFactory.createFrom(notification);
      Set<application.Message> messages = noti.getMessages();
      Set<application.Message> filteredMessages = new HashSet<>();
      application.Message[] messageArray = new application.Message[messages.size()];
      messages.toArray(messageArray);
      for (int idx = 0; idx < messages.size(); idx++) {
        if (skip != null && idx <= skip - 1) {
          continue;
        }
        if (take != null && filteredMessages.size() >= take) {
          break;
        }
        filteredMessages.add(messageArray[idx]);
      }
      return filteredMessages;
    } catch (Exception x) {
      unitOfWork.undo();
      throw new RuntimeException(x);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param notification {@inheritDoc}
   * @return {@inheritDoc}
   */
  public UUID createNotification(application.Notification notification) {

    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
    Date now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
    Notification noti_domain = this.notificationFactory.createFrom(notification);

    try {

      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);

      long timeUntilSend = 0;
      if (noti_domain.sendAt() != null) {
        timeUntilSend = noti_domain.sendAt().getTime() - now.getTime();
      }

      if (timeUntilSend <= 0) {
        for (Message message : noti_domain.messages()) {
          this.smsQueueService.send(noti_domain, message.getId());
          // mark message as PROCESSING via notification interface.
        }
      }

      notificationRepository.add(noti_domain);

      unitOfWork.save();
      return noti_domain.getId();
    } catch (Exception x) {
      // log.
      // throw generic error with reason(s) that can easily be mapped to HTTP status codes.
      // --> for example NOT_FOUND
      x.printStackTrace();
      unitOfWork.undo();
      throw new RuntimeException(x);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param uuid {@inheritDoc}
   * @return {@inheritDoc}
   */
  public application.Notification getNotification(UUID uuid) {

    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
    Notification noti_dm = null;

    try {
      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);
      noti_dm = notificationRepository.get(uuid);
      unitOfWork.save();
    } catch (Exception x) {
      unitOfWork.undo();
      throw x;
    }

    if (noti_dm == null) {
      throw new RuntimeException(
          String.format("Can't find notification with UUID of '%s'", uuid.toString()));
    }

    application.Notification noti_sm = this.applicationNotificationFactory.createFrom(noti_dm);
    return noti_sm;
  }

  /**
   * {@inheritDoc}
   *
   * @param uuid {@inheritDoc}
   */
  public void deleteNotification(UUID uuid) {

    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

    try {
      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);
      notificationRepository.remove(uuid);
      unitOfWork.save();
    } catch (Exception x) {
      unitOfWork.undo();
      throw x;
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param notification {@inheritDoc}
   */
  public void updateNotification(application.Notification notification) {

    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

    try {
      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);
      Notification noti_domain = this.notificationFactory.createFrom(notification);
      notificationRepository.put(noti_domain);
      unitOfWork.save();
    } catch (Exception x) {
      unitOfWork.undo();
      throw x;
    }
  }

  public application.Message getNotificationMessage(UUID notificationUUID, Integer messageID) {
    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

    try {
      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);

      // retrieve notification.
      Notification _notification = notificationRepository.get(notificationUUID);
      unitOfWork.save();
      if (_notification == null) {
        throw new RuntimeException(
            String.format(
                "Can't find notification with UUID of '%s'", notificationUUID.toString()));
      }

      // ensure that the message being updated actually exists.
      if (!_notification.containsMessage(messageID)) {
        throw new RuntimeException(String.format("Can't find message with ID of '%d'", messageID));
      }

      Message message = _notification.message(messageID);
      return this.applicationMessageFactory.createFrom(message);
    } catch (Exception x) {
      unitOfWork.undo();
      throw x;
    }
  }

  public void updateNotificationMessage(UUID notificationUUID, application.Message message) {
    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

    try {
      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);
      Message _message = this.messageFactory.createFrom(message);
      System.out.println("[X] provided status => " + _message.getStatus().toString());

      // retrieve notification.
      Notification _notification = notificationRepository.get(notificationUUID);
      if (_notification == null) {
        throw new RuntimeException(
            String.format(
                "Can't find notification with UUID of '%s'", notificationUUID.toString()));
      }

      // ensure that the message being updated actually exists.
      if (!_notification.containsMessage(_message)) {
        throw new RuntimeException(
            String.format("Can't find message with ID of '%d'", _message.getId()));
      }

      // update the message.
      Message _existing_message = _notification.message(_message.getId());

      // TODO - use state pattern instead to enforce invarients. throw error
      // if the provided status is invalid.
      _existing_message.setStatus(_message.getStatus());
      System.out.println("[X] after modification => " + _existing_message.getStatus().toString());
      notificationRepository.put(_notification);
      unitOfWork.save();
    } catch (Exception x) {
      unitOfWork.undo();
      throw x;
    }
  }
}
