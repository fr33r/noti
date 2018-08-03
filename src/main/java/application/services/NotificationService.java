package application.services;

import domain.Message;
import domain.Notification;
import domain.NotificationFactory;
import infrastructure.MessageQueueService;
import infrastructure.Repository;
import infrastructure.RepositoryFactory;
import infrastructure.SQLUnitOfWork;
import infrastructure.SQLUnitOfWorkFactory;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import javax.inject.Inject;

public final class NotificationService implements application.NotificationService {

  private SQLUnitOfWorkFactory unitOfWorkFactory;
  private RepositoryFactory repositoryFactory;
  private NotificationFactory notificationFactory;
  private application.NotificationFactory applicationNotificationFactory;
  private MessageQueueService smsQueueService;

  @Inject
  public NotificationService(
      SQLUnitOfWorkFactory unitOfWorkFactory,
      RepositoryFactory repositoryFactory,
      MessageQueueService smsQueueService,
      NotificationFactory notificationFactory,
      application.NotificationFactory applicationNotificationFactory) {
    this.unitOfWorkFactory = unitOfWorkFactory;
    this.repositoryFactory = repositoryFactory;
    this.smsQueueService = smsQueueService;
    this.notificationFactory = notificationFactory;
    this.applicationNotificationFactory = applicationNotificationFactory;
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
}
