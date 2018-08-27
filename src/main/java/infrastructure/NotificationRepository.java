package infrastructure;

import domain.Audience;
import domain.EntitySQLFactory;
import domain.Message;
import domain.Notification;
import domain.Target;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Set;
import java.util.UUID;

/**
 * Responsible for retrieving and persisting {@link Notification} entities.
 *
 * @author jonfreer
 */
public final class NotificationRepository extends SQLRepository
    implements Repository<Notification, UUID> {

  private final EntitySQLFactory<Notification, UUID> notificationFactory;
  private final NotificationDataMapper notificationDataMapper;
  private final Tracer tracer;

  /**
   * Constructs a instance of {@link NotificationRepository}.
   *
   * @param unitOfWork The unit of work that this repository will contribute to.
   * @param notificationFactory The factory that reconstitutes {@link Notification} entities.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   */
  public NotificationRepository(
      SQLUnitOfWork unitOfWork,
      EntitySQLFactory<Notification, UUID> notificationFactory,
      NotificationDataMapper notificationDataMapper,
      Tracer tracer) {
    super(unitOfWork);

    this.notificationFactory = notificationFactory;
    this.notificationDataMapper = notificationDataMapper;
    this.tracer = tracer;
  }

  /**
   * Retrieves the notifications matching the provided {@link Query}.
   *
   * @param query The {@link Query} to match against.
   * @return The collection of notifications matching the provided {@link Query}.
   */
  @Override
  public Set<Notification> get(final Query<Notification> query) {
    return query.execute();
  }

  /** Retrieves the entity from the repository by a representation of the entity's identity. */
  @Override
  public Notification get(final UUID uuid) {
    final Span span =
        this.tracer
            .buildSpan("NotificationRepostory#get")
            .asChildOf(this.tracer.activeSpan())
            .start();
    Notification notification = null;
    final String notificationSQL = "SELECT N.* FROM NOTIFICATION AS N WHERE N.UUID = ?;";
    final String targetSQL =
        "SELECT T.* FROM TARGET AS T INNER JOIN NOTIFICATION_TARGET AS NT ON T.UUID = NT.TARGET_UUID WHERE NT.NOTIFICATION_UUID = ?";
    final String messageSQL = "SELECT M.* FROM MESSAGE AS M WHERE M.NOTIFICATION_UUID = ?;";
    final String audiencesSQL =
        "SELECT A.* FROM AUDIENCE AS A INNER JOIN NOTIFICATION_AUDIENCE AS NA ON A.UUID = NA.AUDIENCE_UUID WHERE NA.NOTIFICATION_UUID = ?";
    final String audienceMembersSQL =
        "SELECT AT.AUDIENCE_UUID, T.* FROM ("
            + audiencesSQL
            + ") AS AUDIENCES INNER JOIN AUDIENCE_TARGET AS AT ON AUDIENCES.UUID = AT.AUDIENCE_UUID INNER JOIN TARGET AS T ON AT.TARGET_UUID = T.UUID;";
    try (final Scope scope = this.tracer.scopeManager().activate(span, false);
        final PreparedStatement getNotificationStatement =
            this.getUnitOfWork().createPreparedStatement(notificationSQL);
        final PreparedStatement getTargetsStatement =
            this.getUnitOfWork().createPreparedStatement(targetSQL);
        final PreparedStatement getMessagesStatement =
            this.getUnitOfWork().createPreparedStatement(messageSQL);
        final PreparedStatement getAudiencesStatement =
            this.getUnitOfWork().createPreparedStatement(audiencesSQL);
        final PreparedStatement getAudienceMembersStatement =
            this.getUnitOfWork().createPreparedStatement(audienceMembersSQL)) {
      getNotificationStatement.setString(1, uuid.toString());
      getTargetsStatement.setString(1, uuid.toString());
      getMessagesStatement.setString(1, uuid.toString());
      getAudiencesStatement.setString(1, uuid.toString());
      getAudienceMembersStatement.setString(1, uuid.toString());

      try (final ResultSet notificationRS = getNotificationStatement.executeQuery();
          final ResultSet targetsRS = getTargetsStatement.executeQuery();
          final ResultSet messagesRS = getMessagesStatement.executeQuery();
          final ResultSet audiencesRS = getAudiencesStatement.executeQuery();
          final ResultSet membersRS = getAudienceMembersStatement.executeQuery()) {

        if (notificationRS.next()) {
          notification =
              this.notificationFactory.reconstitute(
                  notificationRS, targetsRS, messagesRS, audiencesRS, membersRS);
        }
      }
      return notification;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }

  /**
   * Places the entity provided into the repository. If the entity provided already exists in the
   * repository, it's state is replaced with the state provided.
   */
  @Override
  public void put(final Notification notification) {
    final Span span =
        this.tracer
            .buildSpan("NotificationRepository#put")
            .asChildOf(this.tracer.activeSpan())
            .start();
    final String sql =
        "UPDATE NOTIFICATION SET MESSAGE = ?, STATUS = ?, SENT_AT = ?, SEND_AT = ? WHERE UUID = ?;";
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      if (this.get(notification.getId()) == null) {
        this.add(notification);
      } else {
        try (final PreparedStatement replaceNotificationStatement =
            this.getUnitOfWork().createPreparedStatement(sql)) {
          replaceNotificationStatement.setString(1, notification.content());
          replaceNotificationStatement.setString(2, notification.status().toString());

          if (notification.sentAt() == null) {
            replaceNotificationStatement.setNull(3, Types.TIMESTAMP);
          } else {
            replaceNotificationStatement.setTimestamp(
                3, new Timestamp(notification.sentAt().getTime()));
          }

          if (notification.sendAt() == null) {
            replaceNotificationStatement.setNull(4, Types.TIMESTAMP);
          } else {
            replaceNotificationStatement.setTimestamp(
                4, new Timestamp(notification.sendAt().getTime()));
          }

          replaceNotificationStatement.setString(5, notification.getId().toString());

          replaceNotificationStatement.executeUpdate();
        } catch (SQLException x) {
          throw new RuntimeException(x);
        }
      }
    } finally {
      span.finish();
    }
  }

  /** Inserts the entity provided into the repository. */
  @Override
  public void add(final Notification notification) {
    final Span span =
        this.tracer
            .buildSpan("NotificationRepository#add")
            .asChildOf(this.tracer.activeSpan())
            .start();
    final String notificationSQL =
        "INSERT INTO NOTIFICATION (UUID, MESSAGE, STATUS, SEND_AT, SENT_AT) VALUES (?, ?, ?, ?, ?);";
    final String associateTargetSQL =
        "INSERT INTO NOTIFICATION_TARGET (NOTIFICATION_UUID, TARGET_UUID) VALUES (?, ?);";
    final String messageSQL =
        "INSERT INTO MESSAGE (ID, `FROM`, `TO`, CONTENT, STATUS, NOTIFICATION_UUID, EXTERNAL_ID) VALUES (?, ?, ?, ?, ?, ?, ?);";
    final String associateAudienceSQL =
        "INSERT INTO NOTIFICATION_AUDIENCE (NOTIFICATION_UUID, AUDIENCE_UUID) VALUES (?, ?);";

    try (final Scope scope = this.tracer.scopeManager().activate(span, false);
        final PreparedStatement createNotificationStatement =
            this.getUnitOfWork().createPreparedStatement(notificationSQL)) {
      createNotificationStatement.setString(1, notification.getId().toString());
      createNotificationStatement.setString(2, notification.content());
      createNotificationStatement.setString(3, notification.status().toString());

      if (notification.sendAt() != null) {
        createNotificationStatement.setTimestamp(4, new Timestamp(notification.sendAt().getTime()));
      } else {
        createNotificationStatement.setNull(4, Types.TIMESTAMP);
      }

      if (notification.sentAt() != null) {
        createNotificationStatement.setTimestamp(5, new Timestamp(notification.sentAt().getTime()));
      } else {
        createNotificationStatement.setNull(5, Types.TIMESTAMP);
      }

      createNotificationStatement.executeUpdate();

      for (Target target : notification.directRecipients()) {
        try (final PreparedStatement associateTargetStatement =
            this.getUnitOfWork().createPreparedStatement(associateTargetSQL)) {
          associateTargetStatement.setString(1, notification.getId().toString());
          associateTargetStatement.setString(2, target.getId().toString());
          associateTargetStatement.executeUpdate();
        }
      }

      for (Audience audience : notification.audiences()) {
        try (final PreparedStatement associateAudienceStatement =
            this.getUnitOfWork().createPreparedStatement(associateAudienceSQL)) {
          associateAudienceStatement.setString(1, notification.getId().toString());
          associateAudienceStatement.setString(2, audience.getId().toString());
          associateAudienceStatement.executeUpdate();
        }
      }

      for (Message message : notification.messages()) {
        try (final PreparedStatement createMessageStatement =
            this.getUnitOfWork().createPreparedStatement(messageSQL)) {
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
    } finally {
      span.finish();
    }
  }

  /** Removes the entity from the repository with the representation of the identity provided. */
  @Override
  public void remove(final UUID uuid) {
    final Span span =
        this.tracer
            .buildSpan("NotificationRepository#remove")
            .asChildOf(this.tracer.activeSpan())
            .start();
    final String deleteNotificationSQL = "DELETE FROM NOTIFICATION WHERE UUID = ?;";
    final String deleteMessagesSQL = "DELETE FROM MESSAGE WHERE NOTIFICATION_UUID = ?;";
    final String deleteTargetAssociationsSQL =
        "DELETE FROM NOTIFICATION_TARGET WHERE NOTIFICATION_UUID = ?;";
    final String deleteAudienceAssociationsSQL =
        "DELETE FROM NOTIFICATION_AUDIENCE WHERE NOTIFICATION_UUID = ?;";
    try (final Scope scope = this.tracer.scopeManager().activate(span, false);
        final PreparedStatement deleteMessagesStatement =
            this.getUnitOfWork().createPreparedStatement(deleteMessagesSQL);
        final PreparedStatement deleteNotificationStatement =
            this.getUnitOfWork().createPreparedStatement(deleteNotificationSQL);
        final PreparedStatement deleteTargetAssociationsStatement =
            this.getUnitOfWork().createPreparedStatement(deleteTargetAssociationsSQL);
        final PreparedStatement deleteAudienceAssociationsStatement =
            this.getUnitOfWork().createPreparedStatement(deleteAudienceAssociationsSQL)) {
      deleteMessagesStatement.setString(1, uuid.toString());
      deleteMessagesStatement.executeUpdate();

      deleteTargetAssociationsStatement.setString(1, uuid.toString());
      deleteTargetAssociationsStatement.executeUpdate();

      deleteAudienceAssociationsStatement.setString(1, uuid.toString());
      deleteAudienceAssociationsStatement.executeUpdate();

      deleteNotificationStatement.setString(1, uuid.toString());
      deleteNotificationStatement.executeUpdate();

    } catch (SQLException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }
}
