package application;

import java.util.UUID;

import application.Notification;

/**
 * Defines the contract for all application services that wish to provide
 * application behavior related to notifications.
 * @author jonfreer
 */
public interface NotificationService {

	/**
	 * Retrieves the notification with the provided unique identifier.
	 * @param uuid The unique identifier for the notification to retrieve.
	 * @return A representation of the notification with the unique identifier provided.
	 */
	Notification getNotification(UUID uuid);

	/**
	 * Creates a new notification with the representation provided.
	 * @param notification The representation of the notification to create.
	 * @return The unique identifier assigned to the newly created notification.
	 */
	UUID createNotification(Notification notification);

	/**
	 * Replaces the existing state of a notification with the representation provided.
	 * @param notification The representation of the notification to overwrite the existing state.
	 */
	void updateNotification(Notification notification);

	/**
	 * Deletes an existing notification.
	 * @param uuid The unique identifier for the notification to delete.
	 */
	void deleteNotification(UUID uuid);
}

