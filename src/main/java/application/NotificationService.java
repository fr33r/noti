package application;

import java.util.Set;
import java.util.UUID;

/**
 * Defines the abstraction that exposes various application operations for notifications.
 *
 * @author Jon Freer
 */
public interface NotificationService {

  /**
   * Retrieves an existing {@link application.Notification}.
   *
   * @param uuid The universally unique identifer of the {@link application.Notification} being
   *     retrieved.
   * @return The {@link application.Notification} with the universally unique identifer provided.
   */
  Notification getNotification(UUID uuid);

  /**
   * Creates a new {@link application.Notification}.
   *
   * @param notification The state of the {@link application.Notification} to create.
   * @return The universally unique identifer of the newly created {@link application.Notification}.
   */
  UUID createNotification(Notification notification);

  /**
   * Replaces the current state of the {@link application.Notification} with the state provided.
   *
   * @param notification The desired state of the {@link application.Notification}.
   */
  void updateNotification(Notification notification);

  /**
   * Deletes an existing {@link application.Notification}.
   *
   * @param uuid The universally unique identifier of the {@link application.Notification} to
   *     delete.
   */
  void deleteNotification(UUID uuid);

  Set<Notification> getNotifications(String externalMessageID, Integer skip, Integer take);

  Integer getNotificationCount();

  Set<Target> getNotificationDirectRecipients(UUID uuid, Integer skip, Integer take);

  Set<Audience> getNotificationAudiences(UUID uuid, Integer skip, Integer take);

  Set<Message> getNotificationMessages(UUID uuid, Integer skip, Integer take);

  Message getNotificationMessage(UUID notificationUUID, Integer messageID);

  void updateNotificationMessage(UUID notificationUUID, Message message);
}
