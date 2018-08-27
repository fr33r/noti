package infrastructure;

import domain.EntitySQLFactory;
import domain.Target;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;

final class TargetDataMapper extends DataMapper {

  private final EntitySQLFactory<Target, UUID> targetFactory;
  private final TargetMetadata targetMetadata;
  private final Logger logger;

  TargetDataMapper(
      SQLUnitOfWork unitOfWork, EntitySQLFactory<Target, UUID> targetFactory, Logger logger) {
    super(unitOfWork);

    this.targetFactory = targetFactory;
    this.targetMetadata = new TargetMetadata();
    this.logger = logger;
  }

  private String findTargetsForNotificationSQL() {
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
            .append(" INNER JOIN NOTIFICATION_TARGET AS NT ON NT.TARGET_UUID = ")
            .append(targetDataMap.getTableAlias())
            .append(".")
            .append(TargetMetadata.UUID)
            .append(" WHERE NT.NOTIFICATION_UUID = ?;");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  Set<Target> findForNotification(UUID notificationUUID) {

    String sql = this.findTargetsForNotificationSQL();

    int index = 0;
    Set<Target> targets = new HashSet<>();
    try (PreparedStatement statement = this.getUnitOfWork().createPreparedStatement(sql)) {
      statement.setString(++index, notificationUUID.toString());
      try (ResultSet rs = statement.executeQuery()) {
        while (rs.next()) {
          targets.add(this.targetFactory.reconstitute(rs));
        }
      }
      return targets;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }
}
