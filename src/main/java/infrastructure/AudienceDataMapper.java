package infrastructure;

import domain.Audience;
import domain.EntitySQLFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;

final class AudienceDataMapper extends DataMapper {

  private final EntitySQLFactory<Audience, UUID> audienceFactory;
  private final AudienceMetadata audienceMetadata;
  private final TargetMetadata targetMetadata;
  private final Logger logger;

  AudienceDataMapper(
      SQLUnitOfWork unitOfWork, EntitySQLFactory<Audience, UUID> audienceFactory, Logger logger) {
    super(unitOfWork);

    this.audienceFactory = audienceFactory;
    this.audienceMetadata = new AudienceMetadata();
    this.targetMetadata = new TargetMetadata();
    this.logger = logger;
  }

  private String findAudiencesForNotificationSQL() {

    DataMap audienceDataMap = this.audienceMetadata.getDataMap();

    List<String> columnNames = audienceDataMap.getAllColumnNames();
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
            .append(AudienceMetadata.UUID)
            .append(" WHERE NA.NOTIFICATION_UUID = ?;");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String findAudienceMembersSQL() {

    DataMap targetDataMap = this.targetMetadata.getDataMap();

    List<String> columnNames = targetDataMap.getAllColumnNames();
    String columns = String.join(", ", columnNames);

    StringBuilder sb =
        new StringBuilder()
            .append("SELECT ")
            .append(columns)
            .append(" FROM ")
            .append(targetDataMap.getTableName())
            .append(" AS ")
            .append(targetDataMap.getTableAlias())
            .append(" INNER JOIN AUDIENCE_TARGET AS AT ON AT.TARGET_UUID = ")
            .append(targetDataMap.getTableAlias())
            .append(".")
            .append(targetDataMap.getColumnNameForField(TargetMetadata.UUID))
            .append(" WHERE AT.AUDIENCE_UUID = ?");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  Set<Audience> findForNotification(UUID notificationUUID) {

    // define SQL.
    String audiencesSQL = this.findAudiencesForNotificationSQL();
    String membersSQL = this.findAudienceMembersSQL();

    int index = 0;
    Set<Audience> audiences = new HashSet<>();
    try (PreparedStatement audiencesStatement =
        this.getUnitOfWork().createPreparedStatement(audiencesSQL)) {
      audiencesStatement.setString(++index, notificationUUID.toString());

      try (ResultSet audiencesRS = audiencesStatement.executeQuery()) {

        String uuid =
            audiencesRS.getString(
                this.audienceMetadata.getDataMap().getColumnNameForField(AudienceMetadata.UUID));

        while (audiencesRS.next()) {
          try (PreparedStatement membersStatement =
              this.getUnitOfWork().createPreparedStatement(membersSQL)) {
            membersStatement.setString(1, uuid);

            try (ResultSet membersRS = membersStatement.executeQuery()) {
              audiences.add(this.audienceFactory.reconstitute(audiencesRS, membersRS));
            }
          }
        }
      }
      return audiences;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }
}
