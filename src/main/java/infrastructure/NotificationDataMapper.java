package infrastructure;

import domain.Audience;
import domain.EntitySQLFactory;
import domain.Message;
import domain.Notification;
import domain.Target;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;

final class NotificationDataMapper extends DataMapper {

  private final EntitySQLFactory<Notification, UUID> notificationFactory;
  private final EntitySQLFactory<Target, UUID> targetFactory;
  private final EntitySQLFactory<Audience, UUID> audienceFactory;
  private final NotificationMetadata notificationMetadata;
  private final MessageMetadata messageMetadata;
  private final TargetMetadata targetMetadata;
  private final AudienceMetadata audienceMetadata;
  private final Logger logger;

  NotificationDataMapper(
      SQLUnitOfWork unitOfWork,
      EntitySQLFactory<Notification, UUID> notificationFactory,
      EntitySQLFactory<Target, UUID> targetFactory,
      EntitySQLFactory<Audience, UUID> audienceFactory,
      Logger logger) {
    super(unitOfWork);

    this.notificationFactory = notificationFactory;
    this.targetFactory = targetFactory;
    this.audienceFactory = audienceFactory;
    this.notificationMetadata = new NotificationMetadata();
    this.messageMetadata = new MessageMetadata();
    this.audienceMetadata = new AudienceMetadata();
    this.targetMetadata = new TargetMetadata();
    this.logger = logger;
  }

  private String findNotificationsSQL(String conditions, String orderBy, String skip, String take) {

    DataMap notificationDataMap = this.notificationMetadata.getDataMap();
    DataMap messageDataMap = this.messageMetadata.getDataMap();

    List<String> columnNames = notificationDataMap.getAllColumnNamesWithAliases();
    String columns = String.join(", ", columnNames);

    StringBuilder sb =
        new StringBuilder()
            .append("SELECT ")
            .append(columns)
            .append(" FROM ")
            .append(notificationDataMap.getTableName())
            .append(" AS ")
            .append(notificationDataMap.getTableAlias());

    if (conditions != null) {
      sb.append(" INNER JOIN ")
          .append(messageDataMap.getTableName())
          .append(" AS ")
          .append(messageDataMap.getTableAlias())
          .append(" ON ")
          .append(notificationDataMap.getTableAlias())
          .append(".")
          .append(notificationDataMap.getColumnNameForField(NotificationMetadata.UUID))
          .append(" = ")
          .append(messageDataMap.getTableAlias())
          .append(".notification_uuid")
          .append(" WHERE ")
          .append(conditions);
    }

    if (orderBy != null) {
      sb.append(" ORDER BY ").append(orderBy);
    }

    if (skip != null) {
      sb.append(" OFFSET ").append(skip);
    }

    if (take != null) {
      sb.append(" LIMIT ").append(take);
    }

    sb.append(";");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String findMessagesSQL() {

    DataMap notificationDataMap = this.notificationMetadata.getDataMap();
    DataMap messageDataMap = this.messageMetadata.getDataMap();

    List<String> columnNames = messageDataMap.getAllColumnNamesWithAliases();
    String columns = String.join(", ", columnNames);

    StringBuilder sb =
        new StringBuilder()
            .append("SELECT ")
            .append(columns)
            .append(" FROM ")
            .append(messageDataMap.getTableName())
            .append(" AS ")
            .append(messageDataMap.getTableAlias())
            .append(" WHERE ")
            .append(messageDataMap.getTableAlias())
            .append(".notification_uuid = ?");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String findAudienceMembersSQL() {

    DataMap targetDataMap = this.targetMetadata.getDataMap();

    List<String> columnNames = targetDataMap.getAllColumnNamesWithAliases();
    String columns = String.join(", ", columnNames);

    StringBuilder sb =
        new StringBuilder()
            .append("SELECT ")
            .append("AT.AUDIENCE_UUID, ")
            .append(columns)
            .append(" FROM (")
            .append(this.findAudiencesSQL())
            .append(") AS AUDIENCES")
            .append(" INNER JOIN AUDIENCE_TARGET AS AT ON AUDIENCES.UUID = AT.AUDIENCE_UUID")
            .append(" INNER JOIN ")
            .append(targetDataMap.getTableName())
            .append(" AS ")
            .append(targetDataMap.getTableAlias())
            .append(" ON AT.TARGET_UUID = ")
            .append(targetDataMap.getTableAlias())
            .append(".")
            .append(targetDataMap.getColumnNameForField(TargetMetadata.UUID));

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String findAudiencesSQL() {

    DataMap audienceDataMap = this.audienceMetadata.getDataMap();

    List<String> columnNames = audienceDataMap.getAllColumnNamesWithAliases();
    String columns = String.join(", ", columnNames);

    StringBuilder sb =
        new StringBuilder()
            .append("SELECT ")
            .append(columns)
            .append(" FROM ")
            .append(audienceDataMap.getTableName())
            .append(" AS ")
            .append(audienceDataMap.getTableAlias())
            .append(" INNER JOIN NOTIFICATION_AUDIENCE AS NA ON NA.AUDIENCE_UUID = ")
            .append(audienceDataMap.getTableAlias())
            .append(".")
            .append(audienceDataMap.getColumnNameForField(AudienceMetadata.UUID))
            .append(" WHERE NA.NOTIFICATION_UUID = ?");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String findRecipientsSQL() {

    DataMap targetDataMap = this.targetMetadata.getDataMap();

    List<String> columnNames = targetDataMap.getAllColumnNamesWithAliases();
    String columns = String.join(", ", columnNames);

    StringBuilder sb =
        new StringBuilder()
            .append("SELECT ")
            .append(columns)
            .append(" FROM ")
            .append(targetDataMap.getTableName())
            .append(" AS ")
            .append(targetDataMap.getTableAlias())
            .append(" INNER JOIN NOTIFICATION_TARGET AS NT ON NT.TARGET_UUID = ")
            .append(targetDataMap.getTableAlias())
            .append(".")
            .append(targetDataMap.getColumnNameForField(TargetMetadata.UUID))
            .append(" WHERE NT.NOTIFICATION_UUID = ?");
    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String findNotificationSQL() {

    DataMap notificationDataMap = this.notificationMetadata.getDataMap();

    List<String> columnNames = notificationDataMap.getAllColumnNamesWithAliases();
    String columns = String.join(", ", columnNames);

    StringBuilder sb =
        new StringBuilder()
            .append("SELECT ")
            .append(columns)
            .append(" FROM ")
            .append(notificationDataMap.getTableName())
            .append(" AS ")
            .append(notificationDataMap.getTableAlias())
            .append(" WHERE ")
            .append(notificationDataMap.getTableAlias())
            .append(".")
            .append(notificationDataMap.getColumnNameForField(NotificationMetadata.UUID))
            .append(" = ?");
    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String updateNotificationSQL() {

    DataMap notificationDataMap = this.notificationMetadata.getDataMap();

    List<String> columnNames = notificationDataMap.getAllColumnNamesWithAliases();
    String columns = String.join(", ", columnNames);

    StringBuilder sb =
        new StringBuilder()
            .append("UPDATE ")
            .append(notificationDataMap.getTableName())
            .append(" SET ")
            .append(notificationDataMap.getColumnNameForField(NotificationMetadata.CONTENT))
            .append(" = ?,")
            .append(notificationDataMap.getColumnNameForField(NotificationMetadata.STATUS))
            .append(" = ?,")
            .append(notificationDataMap.getColumnNameForField(NotificationMetadata.SENT_AT))
            .append(" = ?,")
            .append(notificationDataMap.getColumnNameForField(NotificationMetadata.SEND_AT))
            .append(" = ?")
            .append(" WHERE ")
            .append(notificationDataMap.getColumnNameForField(NotificationMetadata.UUID))
            .append(" = ?");
    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String insertNotificationSQL() {

    DataMap notificationDataMap = this.notificationMetadata.getDataMap();

    List<String> columnNames = notificationDataMap.getAllColumnNamesWithAliases();
    String columns = String.join(", ", columnNames);
    List<String> placeholderList = new ArrayList<>();
    for (int i = 0; i < columnNames.size(); i++) {
      placeholderList.add("?");
    }
    String placeholders = String.join(", ", placeholderList);

    StringBuilder sb =
        new StringBuilder()
            .append("INSERT INTO ")
            .append(notificationDataMap.getTableName())
            .append(" (")
            .append(columns)
            .append(") VALUES (")
            .append(placeholders)
            .append(")");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String insertMessageSQL() {

    DataMap messageDataMap = this.messageMetadata.getDataMap();

    List<String> columnNames = messageDataMap.getAllColumnNamesWithAliases();
    String columns = String.join(", ", columnNames);
    List<String> placeholderList = new ArrayList<>();
    for (int i = 0; i < columnNames.size(); i++) {
      placeholderList.add("?");
    }
    String placeholders = String.join(", ", placeholderList);

    StringBuilder sb =
        new StringBuilder()
            .append("INSERT INTO ")
            .append(messageDataMap.getTableName())
            .append(" (")
            .append(columns)
            .append(") VALUES (")
            .append(placeholders)
            .append(")");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String associateTargetSQL() {
    return "INSERT INTO NOTIFICATION_TARGET (NOTIFICATION_UUID, TARGET_UUID) VALUES (?, ?)";
  }

  private String associateAudienceSQL() {
    return "INSERT INTO NOTIFICATION_AUDIENCE (NOTIFICATION_UUID, AUDIENCE_UUID) VALUES (?, ?)";
  }

  private String deleteNotificationSQL() {

    DataMap notificationDataMap = this.notificationMetadata.getDataMap();

    List<String> columnNames = notificationDataMap.getAllColumnNamesWithAliases();
    String columns = String.join(", ", columnNames);

    StringBuilder sb =
        new StringBuilder()
            .append("DELETE FROM ")
            .append(notificationDataMap.getTableName())
            .append(" WHERE ")
            .append(notificationDataMap.getColumnNameForField(NotificationMetadata.UUID))
            .append(" = ?");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String deleteMessagesSQL() {

    DataMap messageDataMap = this.messageMetadata.getDataMap();

    List<String> columnNames = messageDataMap.getAllColumnNamesWithAliases();
    String columns = String.join(", ", columnNames);

    StringBuilder sb =
        new StringBuilder()
            .append("DELETE FROM ")
            .append(messageDataMap.getTableName())
            .append(" WHERE ")
            .append("NOTIFICATION_UUID = ?");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String dissociateTargetSQL() {
    return "DELETE FROM NOTIFICATION_TARGET WHERE NOTIFICATION_UUID = ?";
  }

  private String dissociateAudienceSQL() {
    return "DELETE FROM NOTIFICATION_AUDIENCE WHERE NOTIFICATION_UUID = ?";
  }

  Set<Notification> find(
      String conditions, String orderBy, String skip, String take, List<Query.QueryArgument> args) {

    // define SQL.
    String notificationSQL = this.findNotificationsSQL(conditions, orderBy, skip, take);
    String messagesSQL = this.findMessagesSQL();
    String audiencesSQL = this.findAudiencesSQL();
    String recipientsSQL = this.findRecipientsSQL();
    String audienceMembersSQL = this.findAudienceMembersSQL();

    Set<Notification> notifications = new HashSet<>();

    // find all matching notifications.
    try (PreparedStatement notificationsStatement =
        this.getUnitOfWork().createPreparedStatement(notificationSQL)) {

      for (Query.QueryArgument arg : args) {
        notificationsStatement.setObject(arg.getIndex(), arg.getValue(), arg.getType());
      }

      try (ResultSet notificationRS = notificationsStatement.executeQuery()) {
        while (notificationRS.next()) {
          String uuidString =
              notificationRS.getString(
                  this.notificationMetadata
                      .getDataMap()
                      .getColumnNameForField(NotificationMetadata.UUID));
          UUID uuid = UUID.fromString(uuidString);

          try (final PreparedStatement getTargetsStatement =
                  this.getUnitOfWork().createPreparedStatement(recipientsSQL);
              final PreparedStatement getMessagesStatement =
                  this.getUnitOfWork().createPreparedStatement(messagesSQL);
              final PreparedStatement getAudiencesStatement =
                  this.getUnitOfWork().createPreparedStatement(audiencesSQL);
              final PreparedStatement getAudienceMembersStatement =
                  this.getUnitOfWork().createPreparedStatement(audienceMembersSQL)) {

            getTargetsStatement.setString(1, uuid.toString());
            getMessagesStatement.setString(1, uuid.toString());
            getAudiencesStatement.setString(1, uuid.toString());
            getAudienceMembersStatement.setString(1, uuid.toString());

            try (final ResultSet targetsRS = getTargetsStatement.executeQuery();
                final ResultSet messagesRS = getMessagesStatement.executeQuery();
                final ResultSet audiencesRS = getAudiencesStatement.executeQuery();
                final ResultSet membersRS = getAudienceMembersStatement.executeQuery()) {
              Notification notification =
                  this.notificationFactory.reconstitute(
                      notificationRS, targetsRS, messagesRS, audiencesRS, membersRS);
              notifications.add(notification);
            }
          }
        }
      }
      return notifications;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  Notification find(final UUID uuid) {

    // define SQL.
    String notificationSQL = this.findNotificationSQL();
    String messagesSQL = this.findMessagesSQL();
    String audiencesSQL = this.findAudiencesSQL();
    String recipientsSQL = this.findRecipientsSQL();
    String audienceMembersSQL = this.findAudienceMembersSQL();

    Notification notification = null;

    // find matching notification.
    try (final PreparedStatement getNotificationStatement =
            this.getUnitOfWork().createPreparedStatement(notificationSQL);
        final PreparedStatement getTargetsStatement =
            this.getUnitOfWork().createPreparedStatement(recipientsSQL);
        final PreparedStatement getMessagesStatement =
            this.getUnitOfWork().createPreparedStatement(messagesSQL);
        final PreparedStatement getAudiencesStatement =
            this.getUnitOfWork().createPreparedStatement(audiencesSQL);
        final PreparedStatement getAudienceMembersStatement =
            this.getUnitOfWork().createPreparedStatement(audienceMembersSQL)) {

      int index = 1;
      getNotificationStatement.setString(index, uuid.toString());
      getTargetsStatement.setString(index, uuid.toString());
      getMessagesStatement.setString(index, uuid.toString());
      getAudiencesStatement.setString(index, uuid.toString());
      getAudienceMembersStatement.setString(index, uuid.toString());

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
    }
  }

  void insert(final Notification notification) {

    final String notificationSQL = this.insertNotificationSQL();
    final String associateTargetSQL = this.associateTargetSQL();
    final String messageSQL = this.insertMessageSQL();
    final String associateAudienceSQL = this.associateAudienceSQL();

    try (final PreparedStatement createNotificationStatement =
        this.getUnitOfWork().createPreparedStatement(notificationSQL)) {
      int index = 0;
      createNotificationStatement.setString(++index, notification.getId().toString());
      createNotificationStatement.setString(++index, notification.content());
      createNotificationStatement.setString(++index, notification.status().toString());

      if (notification.sendAt() != null) {
        createNotificationStatement.setTimestamp(
            ++index, new Timestamp(notification.sendAt().getTime()));
      } else {
        createNotificationStatement.setNull(++index, Types.TIMESTAMP);
      }

      if (notification.sentAt() != null) {
        createNotificationStatement.setTimestamp(
            ++index, new Timestamp(notification.sentAt().getTime()));
      } else {
        createNotificationStatement.setNull(++index, Types.TIMESTAMP);
      }

      createNotificationStatement.executeUpdate();

      for (Target target : notification.directRecipients()) {
        try (final PreparedStatement associateTargetStatement =
            this.getUnitOfWork().createPreparedStatement(associateTargetSQL)) {
          index = 0;
          associateTargetStatement.setString(++index, notification.getId().toString());
          associateTargetStatement.setString(++index, target.getId().toString());
          associateTargetStatement.executeUpdate();
        }
      }

      for (Audience audience : notification.audiences()) {
        try (final PreparedStatement associateAudienceStatement =
            this.getUnitOfWork().createPreparedStatement(associateAudienceSQL)) {
          index = 0;
          associateAudienceStatement.setString(++index, notification.getId().toString());
          associateAudienceStatement.setString(++index, audience.getId().toString());
          associateAudienceStatement.executeUpdate();
        }
      }

      for (Message message : notification.messages()) {
        try (final PreparedStatement createMessageStatement =
            this.getUnitOfWork().createPreparedStatement(messageSQL)) {
          index = 0;
          createMessageStatement.setInt(++index, message.getId());
          createMessageStatement.setString(++index, message.getFrom().toE164());
          createMessageStatement.setString(++index, message.getTo().toE164());
          createMessageStatement.setString(++index, message.getContent());
          createMessageStatement.setString(++index, message.getStatus().toString());
          createMessageStatement.setString(++index, notification.getId().toString());
          createMessageStatement.setString(++index, message.getExternalId());
          createMessageStatement.executeUpdate();
        }
      }

    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  void update(final Notification notification) {

    String notificationSQL = this.updateNotificationSQL();
    try (final PreparedStatement updateNotificationStatement =
        this.getUnitOfWork().createPreparedStatement(notificationSQL)) {
      int index = 0;
      updateNotificationStatement.setString(++index, notification.content());
      updateNotificationStatement.setString(++index, notification.status().toString());

      if (notification.sentAt() == null) {
        updateNotificationStatement.setNull(++index, Types.TIMESTAMP);
      } else {
        updateNotificationStatement.setTimestamp(
            ++index, new Timestamp(notification.sentAt().getTime()));
      }

      if (notification.sendAt() == null) {
        updateNotificationStatement.setNull(++index, Types.TIMESTAMP);
      } else {
        updateNotificationStatement.setTimestamp(
            ++index, new Timestamp(notification.sendAt().getTime()));
      }

      updateNotificationStatement.setString(++index, notification.getId().toString());

      updateNotificationStatement.executeUpdate();
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  void delete(final UUID uuid) {

    final String deleteNotificationSQL = this.deleteNotificationSQL();
    final String deleteMessagesSQL = this.deleteMessagesSQL();
    final String deleteTargetAssociationsSQL = this.dissociateTargetSQL();
    final String deleteAudienceAssociationsSQL = this.dissociateAudienceSQL();

    try (final PreparedStatement deleteMessagesStatement =
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
    }
  }
}
