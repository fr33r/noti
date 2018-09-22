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
import javax.inject.Named;
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
      @Named("infrastructure.NotificationDataMapper") Logger logger) {
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

    if (take != null) {
      sb.append(" LIMIT ").append(take);
    }

    if (skip != null) {
      sb.append(" OFFSET ").append(skip);
    }

    sb.append(";");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String findMessagesSQL() {

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

    StringBuilder sb =
        new StringBuilder()
            .append("UPDATE ")
            .append(notificationDataMap.getTableName())
            .append(" SET ")
            .append(notificationDataMap.getColumnNameForField(NotificationMetadata.CONTENT))
            .append(" = ?, ")
            .append(notificationDataMap.getColumnNameForField(NotificationMetadata.STATUS))
            .append(" = ?, ")
            .append(notificationDataMap.getColumnNameForField(NotificationMetadata.SENT_AT))
            .append(" = ?, ")
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
    String sql = this.insertSQL(1, notificationDataMap);
    this.logger.debug(sql);
    return sql;
  }

  private String insertMessageSQL() {
    DataMap messageDataMap = this.messageMetadata.getDataMap();
    String sql = this.insertSQL(1, messageDataMap);
    this.logger.debug(sql);
    return sql;
  }

  private String updateMessageSQL() {
    DataMap messageDataMap = this.messageMetadata.getDataMap();
    StringBuilder sb =
        new StringBuilder()
            .append("UPDATE ")
            .append(messageDataMap.getTableName())
            .append(" SET ")
            .append(messageDataMap.getColumnNameForField(MessageMetadata.CONTENT))
            .append(" = ?, ")
            .append(messageDataMap.getColumnNameForField(MessageMetadata.TO))
            .append(" = ?, ")
            .append(messageDataMap.getColumnNameForField(MessageMetadata.FROM))
            .append(" = ?, ")
            .append(messageDataMap.getColumnNameForField(MessageMetadata.EXTERNAL_ID))
            .append(" = ?, ")
            .append(messageDataMap.getColumnNameForField(MessageMetadata.STATUS))
            .append(" = ?")
            .append(" WHERE ")
            .append(messageDataMap.getColumnNameForField(MessageMetadata.ID))
            .append(" = ? AND NOTIFICATION_UUID = ?");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String insertMessagesSQL(int numberOfMessages) {
    DataMap messageDataMap = this.messageMetadata.getDataMap();
    String sql = this.insertSQL(numberOfMessages, messageDataMap);
    this.logger.debug(sql);
    return sql;
  }

  private String deleteMessagesSQL(int numberOfMessages) {
    DataMap messageDataMap = this.messageMetadata.getDataMap();
    String matchCriteria =
        new StringBuilder()
            .append(messageDataMap.getTableAlias())
            .append(".")
            .append(messageDataMap.getColumnNameForField(MessageMetadata.ID))
            .append(" = ?)")
            .toString();
    String sql = this.deleteSQL(numberOfMessages, messageDataMap, matchCriteria);
    this.logger.debug(sql);
    return sql;
  }

  private String associateTargetSQL() {
    return this.associateTargetsSQL(1);
  }

  private String associateTargetsSQL(int numberOfTargets) {
    List<String> columns = new ArrayList<>();
    columns.add("NOTIFICATION_UUID");
    columns.add("TARGET_UUID");
    String tableName = "NOTIFICATION_TARGET";
    String sql = this.insertSQL(numberOfTargets, tableName, columns);
    this.logger.debug(sql);
    return sql;
  }

  private String disassociateTargetsSQL(int numberOfTargets) {
    String tableName = "NOTIFICATION_TARGET";
    String matchCriteria = "NOTIFICATION_UUID = ? AND NOTIFICATION_TARGET = ?";
    return this.deleteSQL(numberOfTargets, tableName, matchCriteria);
  }

  private String associateAudienceSQL() {
    return this.associateAudiencesSQL(1);
  }

  private String associateAudiencesSQL(int numberOfAudiences) {
    List<String> columns = new ArrayList<>();
    columns.add("NOTIFICATION_UUID");
    columns.add("AUDIENCE_UUID");
    String tableName = "NOTIFICATION_AUDIENCE";
    String sql = this.insertSQL(numberOfAudiences, tableName, columns);
    this.logger.debug(sql);
    return sql;
  }

  private String disassociateAudiencesSQL(int numberOfAudiences) {
    String tableName = "NOTIFICATION_AUDIENCE";
    String matchCriteria = "NOTIFICATION_UUID = ? AND AUDIENCE_UUID = ?";
    String sql = this.deleteSQL(numberOfAudiences, tableName, matchCriteria);
    this.logger.debug(sql);
    return sql;
  }

  private String deleteNotificationSQL() {
    DataMap notificationDataMap = this.notificationMetadata.getDataMap();
    String matchCriteria =
        new StringBuilder()
            .append(notificationDataMap.getColumnNameForField(NotificationMetadata.UUID))
            .append(" = ?")
            .toString();

    String sql = this.deleteSQL(1, notificationDataMap, matchCriteria);
    this.logger.debug(sql);
    return sql;
  }

  private String deleteMessagesSQL() {
    DataMap messageDataMap = this.messageMetadata.getDataMap();
    String matchCriteria = "NOTIFICATION_UUID = ?";
    String sql = this.deleteSQL(1, messageDataMap, matchCriteria);
    this.logger.debug(sql);
    return sql;
  }

  private String dissociateTargetSQL() {
    String tableName = "NOTIFICATION_TARGET";
    String matchCriteria = "NOTIFICATION_UUID = ?";
    String sql = this.deleteSQL(1, tableName, matchCriteria);
    this.logger.debug(sql);
    return sql;
  }

  private String dissociateAudienceSQL() {
    String tableName = "NOTIFICATION_AUDIENCE";
    String matchCriteria = "NOTIFICATION_UUID = ?";
    String sql = this.deleteSQL(1, tableName, matchCriteria);
    this.logger.debug(sql);
    return sql;
  }

  private String countNotificationsSQL() {
    DataMap notificationDataMap = this.notificationMetadata.getDataMap();
    StringBuilder sb =
        new StringBuilder()
            .append("SELECT ")
            .append("COUNT(*) ")
            .append("FROM ")
            .append(notificationDataMap.getTableName())
            .append(" AS ")
            .append(notificationDataMap.getTableAlias());
    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
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

  // TODO - should do a diff between targets and audiences.
  void update(final Notification notification) {

    String notificationSQL = this.updateNotificationSQL();
    Notification existingNotification = this.find(notification.getId());
    if (existingNotification == null) return;

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

      Set<UUID> recipientsToAssociate = new HashSet<>();
      Set<UUID> recipeintsToDisassociate = new HashSet<>();

      // determine which recipients are being added.
      for (Target recipient : notification.directRecipients()) {
        if (!existingNotification.directRecipients().contains(recipient)) {
          recipientsToAssociate.add(recipient.getId());
        }
      }

      // determine which recipients are being removed.
      for (Target recipient : existingNotification.directRecipients()) {
        if (!notification.directRecipients().contains(recipient)) {
          recipeintsToDisassociate.add(recipient.getId());
        }
      }

      Set<UUID> audiencesToAssociate = new HashSet<>();
      Set<UUID> audiencesToDisassociate = new HashSet<>();

      // determine which audiences are being added.
      for (Audience audience : notification.audiences()) {
        if (!existingNotification.audiences().contains(audience)) {
          audiencesToAssociate.add(audience.getId());
        }
      }

      // determine which audiences are being removed.
      for (Audience audience : existingNotification.audiences()) {
        if (!notification.audiences().contains(audience)) {
          audiencesToDisassociate.add(audience.getId());
        }
      }

      Set<Integer> messagesToInsert = new HashSet<>();
      Set<Integer> messagesToDelete = new HashSet<>();
      Set<Integer> messagesToUpdate = new HashSet<>();

      // determine which messages are being added.
      for (Message message : notification.messages()) {
        if (!existingNotification.messages().contains(message)) {
          messagesToInsert.add(message.getId());
        }
      }

      // determine which messages are being removed.
      for (Message message : existingNotification.messages()) {
        if (!notification.messages().contains(message)) {
          messagesToDelete.add(message.getId());
        }
      }

      // determine which messages are being updated.
      for (Message message : notification.messages()) {
        if (existingNotification.messages().contains(message)) {
          messagesToUpdate.add(message.getId());
        }
      }

      if (!audiencesToAssociate.isEmpty()) {
        String associateAudiencesSQL = this.associateAudiencesSQL(audiencesToAssociate.size());
        try (final PreparedStatement associateAudienceStatement =
            this.getUnitOfWork().createPreparedStatement(associateAudiencesSQL)) {
          index = 0;
          for (UUID uuid : audiencesToAssociate) {
            associateAudienceStatement.setString(++index, notification.getId().toString());
            associateAudienceStatement.setString(++index, uuid.toString());
          }
          associateAudienceStatement.executeUpdate();
        }
      }

      if (!audiencesToDisassociate.isEmpty()) {
        String disassociateAudiencesSQL =
            this.disassociateAudiencesSQL(audiencesToDisassociate.size());
        try (final PreparedStatement disassociateAudiencesStatement =
            this.getUnitOfWork().createPreparedStatement(disassociateAudiencesSQL)) {
          index = 0;
          for (UUID uuid : audiencesToDisassociate) {
            disassociateAudiencesStatement.setString(++index, notification.getId().toString());
            disassociateAudiencesStatement.setString(++index, uuid.toString());
          }
          disassociateAudiencesStatement.executeUpdate();
        }
      }

      if (!messagesToInsert.isEmpty()) {
        String insertMessagesSQL = this.insertMessagesSQL(messagesToInsert.size());
        try (final PreparedStatement insertMessagesStatement =
            this.getUnitOfWork().createPreparedStatement(insertMessagesSQL)) {
          index = 0;
          for (Integer id : messagesToInsert) {
            insertMessagesStatement.setString(++index, notification.getId().toString());
            insertMessagesStatement.setInt(++index, id);
          }
          insertMessagesStatement.executeUpdate();
        }
      }

      if (!messagesToDelete.isEmpty()) {
        String deleteMessagesSQL = this.deleteMessagesSQL(messagesToDelete.size());
        try (final PreparedStatement deleteMessagesStatement =
            this.getUnitOfWork().createPreparedStatement(deleteMessagesSQL)) {
          index = 0;
          for (Integer id : messagesToDelete) {
            deleteMessagesStatement.setString(++index, notification.getId().toString());
            deleteMessagesStatement.setInt(++index, id);
          }
          deleteMessagesStatement.executeUpdate();
        }
      }

      if (!messagesToUpdate.isEmpty()) {
        String updateMessageSQL = this.updateMessageSQL();
        for (Integer id : messagesToUpdate) {
          index = 0;
          Message message = notification.message(id);
          try (final PreparedStatement updateMessageStatement =
              this.getUnitOfWork().createPreparedStatement(updateMessageSQL)) {
            updateMessageStatement.setString(++index, message.getContent());
            updateMessageStatement.setString(++index, message.getTo().toE164());
            updateMessageStatement.setString(++index, message.getFrom().toE164());
            updateMessageStatement.setString(++index, message.getExternalId());
            updateMessageStatement.setString(++index, message.getStatus().toString());
            updateMessageStatement.setInt(++index, id);
            updateMessageStatement.setString(++index, notification.getId().toString());
            updateMessageStatement.executeUpdate();
          }
        }
      }
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

      int index = 1;
      deleteMessagesStatement.setString(index, uuid.toString());
      deleteMessagesStatement.executeUpdate();

      deleteTargetAssociationsStatement.setString(index, uuid.toString());
      deleteTargetAssociationsStatement.executeUpdate();

      deleteAudienceAssociationsStatement.setString(index, uuid.toString());
      deleteAudienceAssociationsStatement.executeUpdate();

      deleteNotificationStatement.setString(index, uuid.toString());
      deleteNotificationStatement.executeUpdate();

    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  int count() {

    final String countNotificationsSQL = this.countNotificationsSQL();

    try (final PreparedStatement countNotificationsStatement =
            this.getUnitOfWork().createPreparedStatement(countNotificationsSQL);
        final ResultSet rs = countNotificationsStatement.executeQuery()) {
      int index = 1;
      rs.next();
      return rs.getInt(index);
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }
}
