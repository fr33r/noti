package application.services;

import application.InternalErrorException;
import application.NotFoundException;
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
import javax.inject.Named;
import org.slf4j.Logger;

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
  private final Logger logger;

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
      Tracer tracer,
      @Named("application.services.NotificationService") Logger logger) {
    this.unitOfWorkFactory = unitOfWorkFactory;
    this.repositoryFactory = repositoryFactory;
    this.smsQueueService = smsQueueService;
    this.notificationFactory = notificationFactory;
    this.messageFactory = messageFactory;
    this.applicationMessageFactory = applicationMessageFactory;
    this.applicationNotificationFactory = applicationNotificationFactory;
    this.queryFactory = queryFactory;
    this.tracer = tracer;
    this.logger = logger;
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
      this.logger.info("Retrieved {} matching notifications.", notifications.size());
      return notifications;
    } catch (Exception x) {
      unitOfWork.undo();
      String errorMessage = "An error occurred when retrieving notifications.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
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
      String errorMessage = "An error occurred when retrieving the total number of notifications.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
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
        String errorMessage = "Can't find notification.";
        String detailedMessage =
            String.format("Can't find notification with UUID of '%s'", uuid.toString());
        this.logger.warn(detailedMessage);
        throw new NotFoundException(errorMessage, detailedMessage);
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
      String errorMessage =
          "An error occurred when retrieving the direct recipients of the notification.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
    }
  }

  public Set<application.Audience> getNotificationAudiences(UUID uuid, Integer skip, Integer take) {
    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
    try {
      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);
      domain.Notification notification = notificationRepository.get(uuid);
      if (notification == null) {
        String errorMessage = "Can't find notification.";
        String detailedMessage =
            String.format("Can't find notification with UUID of '%s'", uuid.toString());
        this.logger.warn(detailedMessage);
        throw new NotFoundException(errorMessage, detailedMessage);
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
      String errorMessage = "An error occurred when retrieving the audiences of the notification.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
    }
  }

  public Set<application.Message> getNotificationMessages(UUID uuid, Integer skip, Integer take) {
    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
    try {
      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);
      domain.Notification notification = notificationRepository.get(uuid);
      if (notification == null) {
        String errorMessage = "Can't find notification.";
        String detailedMessage =
            String.format("Can't find notification with UUID of '%s'", uuid.toString());
        this.logger.warn(detailedMessage);
        throw new NotFoundException(errorMessage, detailedMessage);
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
      String errorMessage = "An error occurred when retrieving the messages of the notification.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
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
        this.logger.info(
            "Notification has {} milliseconds until it should be sent.", timeUntilSend);
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
      unitOfWork.undo();
      String errorMessage = "An error occurred when creating the notification.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
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
    Notification notification = null;

    try {
      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);
      notification = notificationRepository.get(uuid);
      unitOfWork.save();
    } catch (Exception x) {
      unitOfWork.undo();
      String errorMessage = "An error occurred when retrieving the notification.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
    }

    if (notification == null) {
      String errorMessage = "Can't find notification.";
      String detailedMessage =
          String.format("Can't find notification with UUID of '%s'", uuid.toString());
      this.logger.warn(detailedMessage);
      throw new NotFoundException(errorMessage, detailedMessage);
    }

    return this.applicationNotificationFactory.createFrom(notification);
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
      String errorMessage = "An error occurred when deleting the notification.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
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
      String errorMessage = "An error occurred when updating the notification.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
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
        String errorMessage = "Can't find notification.";
        String detailedMessage =
            String.format("Can't find notification with UUID of '%s'", notificationUUID.toString());
        this.logger.warn(detailedMessage);
        throw new NotFoundException(errorMessage, detailedMessage);
      }

      // ensure that the message being retrieved actually exists.
      if (!_notification.containsMessage(messageID)) {
        String errorMessage = "Can't find message.";
        String detailedMessage = String.format("Can't find a message with ID '%d'.", messageID);
        this.logger.warn(detailedMessage);
        throw new NotFoundException(errorMessage, detailedMessage);
      }

      Message message = _notification.message(messageID);
      return this.applicationMessageFactory.createFrom(message);
    } catch (Exception x) {
      unitOfWork.undo();
      String errorMessage = "An error occurred when retrieving the notification messages.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
    }
  }

  public void updateNotificationMessage(UUID notificationUUID, application.Message message) {
    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

    try {
      Repository<Notification, UUID> notificationRepository =
          this.repositoryFactory.createNotificationRepository(unitOfWork);
      Message _message = this.messageFactory.createFrom(message);

      // retrieve notification.
      Notification _notification = notificationRepository.get(notificationUUID);
      if (_notification == null) {
        String errorMessage = "Can't find notification.";
        String detailedMessage =
            String.format("Can't find notification with UUID of '%s'", notificationUUID.toString());
        this.logger.warn(detailedMessage);
        throw new NotFoundException(errorMessage, detailedMessage);
      }

      // ensure that the message being updated actually exists.
      if (!_notification.containsMessage(_message)) {
        String errorMessage = "Can't find message.";
        String detailedMessage =
            String.format("Can't find a message with ID '%d'.", message.getID());
        this.logger.warn(detailedMessage);
        throw new NotFoundException(errorMessage, detailedMessage);
      }

      // update the message.
      Message _existing_message = _notification.message(_message.getId());

      // TODO - use state pattern instead to enforce invarients. throw error
      // if the provided status is invalid.
      _existing_message.setStatus(_message.getStatus());
      notificationRepository.put(_notification);
      unitOfWork.save();
    } catch (Exception x) {
      unitOfWork.undo();
      String errorMessage = "An error occurred when updating the notification messages.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
    }
  }
}
