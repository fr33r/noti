package api.representations.json;

import api.representations.Representation;
import api.representations.json.Audience;
import api.representations.json.NotificationStatus;
import api.representations.json.Target;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

/**
 * Defines the 'application/json' representation of a Notification resource.
 *
 * @author Jon Freer
 */
public final class Notification extends Representation {

	private final UUID uuid;
	private final String content;
	private final Date sentAt;
	private final Date sendAt;
	private final NotificationStatus status;
	private final Set<Target> targets;
	private final Set<Audience> audiences;

	/**
	 * Constructs an empty instance of {@link Notification}.
	 */
	public Notification() {
		super(MediaType.APPLICATION_JSON_TYPE);

		this.uuid = null;
		this.content = null;
		this.sentAt = null;
		this.sendAt = null;
		this.status = null;
		this.targets = new HashSet<>();
		this.audiences = new HashSet<>();
	}

	/**
	 * Constructs a fully initialized instances of {@link Notification}.
	 *
	 * @param uuid The universally unique identifier of the Notification resource.
	 * @param content The information being communicated within the notification.
	 * @param status The status of the notification in terms of its delivery to its audiences and targets.
	 * @param targets Explicit recipients that should receive this notification.
	 * @param audiences Broader audiences that should receive this notification.
	 * @param sendAt States when the notification should be sent to its targets and audiences.
	 * @param sentAt States when the notification was sent to all of its targets and all of its audiences.
	 */
	public Notification(
			final UUID uuid,
			final String content, 
			final NotificationStatus status,
			final Set<Target> targets,
			final Set<Audience> audiences,
			final Date sendAt,
			final Date sentAt
		) {
			super(MediaType.APPLICATION_JSON_TYPE);

			this.uuid = uuid;
			this.content = content;
			this.status = status;
			this.sendAt = sendAt;
			this.sentAt = sentAt;
			this.targets = targets == null ? new HashSet<>() : targets;
			this.audiences = audiences == null ? new HashSet<>() : audiences;
		}

	/**
	 * Retrieves the universally unique identifier of this notification.
	 *
	 * @return The universally unique identifier of this notification.
	 */
	public UUID getUUID(){
		return this.uuid;
	}

	/**
	 * Retrieves the information being communicated within this notification.
	 *
	 * @return The information being communicated within this notification.
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Retrieves the status the delivery status of this notification.
	 * 
	 * @return The status of the notification in terms of its delivery to its audiences and targets.
	 */
	public NotificationStatus getStatus() {
		return this.status;
	}

	/**
	 * Retrieves the date and time in which this notification is to be sent.
	 * A value of {@code null} indicates that the notification should be sent immediately.
	 *
	 * @return States when the notification should be sent to its targets and audiences.
	 */
	public Date getSendAt() {
		return this.sendAt;
	}

	/**
	 * Retrieves the date and time in which this notification was sent to all of its targets and audiences.
	 * A value of {@code null} indicates that the notification has not been sent to all of its targets and audiences.
	 * Please refer to {@link NotificationStatus} to gain more insight into the progress of the notification delivery.
	 *
	 * @return States when the notification was sent to all of its targets and all of its audiences.
	 */
	public Date getSentAt() {
		return this.sentAt;
	}

	/**
	 * Retrieves the explicit recipients that should receive this notification.
	 *
	 * @return Explicit recipients that should receive this notification.
	 */
	public Set<Target> getTargets() {
		return this.targets;
	}

	/**
	 * Retrieves the named audiences that should receive this notification.
	 *
	 * @return Broader audiences that should receive this notification.
	 */
	public Set<Audience> getAudiences() {
		return this.audiences;
	}
}
