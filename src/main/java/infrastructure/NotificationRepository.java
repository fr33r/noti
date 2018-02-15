package infrastructure;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.UUID;

import domain.EntitySQLFactory;
import domain.Message;
import domain.Notification;
import domain.Target;

/**
 * Responsible for retrieving and persisting {@link Notification} entities.
 * @author jonfreer
 *
 */
public final class NotificationRepository extends SQLRepository implements Repository<Notification, UUID> {

	private final EntitySQLFactory<Notification, UUID> notificationFactory;

	/**
	 * Constructs a instance of {@link NotificationRepository}.
	 * @param unitOfWork The unit of work that this repository will contribute to.
	 * @param notificationFactory The factory that reconstitutes {@link Notification} entities.
	 */
	public NotificationRepository(
		SQLUnitOfWork unitOfWork, 
		EntitySQLFactory<Notification, UUID> notificationFactory
	) {
		super(unitOfWork);
		this.notificationFactory = notificationFactory;
	}

	/**
	 *	Retrieves the entity from the repository by a representation of the entity's identity.
	 */
	@Override
	public Notification get(final UUID uuid) {

		Notification notification = null;
		final String notificationSQL =
			"SELECT N.* FROM NOTIFICATION AS N WHERE N.UUID = ?;";
		final String targetSQL =
			"SELECT T.* FROM TARGET AS T INNER JOIN NOTIFICATION_TARGET AS NT ON T.UUID = NT.TARGET_UUID WHERE NT.NOTIFICATION_UUID = ?";
		final String tagSQL =
			"SELECT TT.TARGET_UUID, TG.* FROM (" + targetSQL + ") AS TARGETS INNER JOIN TARGET_TAG AS TT ON TARGETS.UUID = TT.TARGET_UUID INNER JOIN TAG AS TG ON TT.TAG_UUID = TG.UUID;";
		final String messageSQL =
			"SELECT M.* FROM MESSAGE AS M WHERE M.NOTIFICATION_UUID = ?;";
		try (
			final PreparedStatement getNotificationStatement = 
				this.getUnitOfWork().createPreparedStatement(notificationSQL);
			final PreparedStatement getTargetsStatement = 
				this.getUnitOfWork().createPreparedStatement(targetSQL);
			final PreparedStatement getTagsStatement = 
				this.getUnitOfWork().createPreparedStatement(tagSQL);
			final PreparedStatement getMessagesStatement = 
				this.getUnitOfWork().createPreparedStatement(messageSQL)
		) {
			getNotificationStatement.setString(1, uuid.toString());
			getTargetsStatement.setString(1, uuid.toString());
			getTagsStatement.setString(1, uuid.toString());
			getMessagesStatement.setString(1, uuid.toString());

			try (
				final ResultSet notificationRS = getNotificationStatement.executeQuery();
				final ResultSet targetsRS = getTargetsStatement.executeQuery();
				final ResultSet tagsRS = getTagsStatement.executeQuery();
				final ResultSet messagesRS = getMessagesStatement.executeQuery()
			) {
				notification =
					this.notificationFactory.reconstitute(
						notificationRS,
						targetsRS,
						tagsRS,
						messagesRS
					);
			}

		} catch (SQLException x){
			throw new RuntimeException(x);
		}
		return notification;
	}

	/**
	 *	Places the entity provided into the repository. If the entity provided already exists
	 *	in the repository, it's state is replaced with the state provided.
	 */
	@Override
	public void put(final Notification notification) {

		final String sql =
			"UPDATE NOTIFICATION SET MESSAGE = ?, STATUS = ?, SENT_AT = ?, SEND_AT = ? WHERE UUID = ?;";
		if(this.get(notification.getId()) == null) {
			this.add(notification);
		} else {
			try(final PreparedStatement pStatement = this.getUnitOfWork().createPreparedStatement(sql)) {
				pStatement.setString(1, notification.content());
				pStatement.setString(2, notification.content().toString());
				pStatement.setTimestamp(3, new Timestamp(notification.sentAt().getTime()));
				pStatement.setTimestamp(4, new Timestamp(notification.sendAt().getTime()));
				pStatement.executeUpdate();
			} catch (SQLException x) {
				throw new RuntimeException(x);
			}
		}
	}

	/**
	 *	Inserts the entity provided into the repository. 
	 */
	@Override
	public void add(final Notification notification) {

		final String notificationSQL =
			"INSERT INTO NOTIFICATION (UUID, MESSAGE, STATUS, SEND_AT, SENT_AT) VALUES (?, ?, ?, ?, ?);";
		final String associateTargetSQL =
			"INSERT INTO NOTIFICATION_TARGET (NOTIFICATION_UUID, TARGET_UUID) VALUES (?, ?);";
		final String messageSQL =
			"INSERT INTO MESSAGE (ID, `FROM`, `TO`, CONTENT, STATUS, NOTIFICATION_UUID, EXTERNAL_ID) VALUES (?, ?, ?, ?, ?, ?, ?);";
		try(
			final PreparedStatement createNotificationStatement =
				this.getUnitOfWork().createPreparedStatement(notificationSQL)
		) {
			createNotificationStatement.setString(1, notification.getId().toString());
			createNotificationStatement.setString(2, notification.content());
			createNotificationStatement.setString(3, notification.status().toString());
			
			if(notification.sendAt() != null) {
				createNotificationStatement.setTimestamp(4, new Timestamp(notification.sendAt().getTime()));
			} else {
				createNotificationStatement.setNull(4, Types.TIMESTAMP);
			}

			if(notification.sentAt() != null) {
				createNotificationStatement.setTimestamp(5, new Timestamp(notification.sentAt().getTime()));
			} else {
				createNotificationStatement.setNull(5, Types.TIMESTAMP);
			}

			createNotificationStatement.executeUpdate();

			for(Target target : notification.directRecipients()) {
				try(final PreparedStatement associateTargetStatement =
					this.getUnitOfWork().createPreparedStatement(associateTargetSQL)
				) {
					associateTargetStatement.setString(1, notification.getId().toString());
					associateTargetStatement.setString(2, target.getId().toString());
					associateTargetStatement.executeUpdate();
				}
			}

			for(Message message : notification.messages()) {
				try(final PreparedStatement createMessageStatement =
					this.getUnitOfWork().createPreparedStatement(messageSQL)
				) {
					createMessageStatement.setInt(1, message.getId());
					createMessageStatement.setString(2, message.getFrom().toE164());
					createMessageStatement.setString(3, message.getTo().toE164());
					createMessageStatement.setString(4, message.getContent());
					createMessageStatement.setString(5, message.getStatus().toString());
					createMessageStatement.setString(6, notification.getId().toString());
					createMessageStatement.setString(7, message.getExternalId());
					createMessageStatement.executeUpdate();
				}
			}

		} catch (SQLException x) {
			throw new RuntimeException(x);
		}
	}

	/**
	 *	Removes the entity from the repository with the representation of the identity provided.
	 */
	@Override
	public void remove(final UUID uuid) {

		final String sql = "DELETE FROM NOTIFICATION WHERE UUID = ?;";
		try(
			final PreparedStatement pStatement =
				this.getUnitOfWork().createPreparedStatement(sql)
		) {
			pStatement.setString(1, uuid.toString());
			pStatement.executeUpdate();
		} catch (SQLException x) {
			throw new RuntimeException(x);
		}
	}
}
