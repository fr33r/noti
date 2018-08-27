package infrastructure;

import domain.Audience;
import domain.EntitySQLFactory;
import domain.Notification;
import domain.Target;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;

// consider making this package visible only?
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
            .append(" WHERE NT.NOTIFICATION_UUID =?;");
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
}
