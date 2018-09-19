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
            .append(targetDataMap.getColumnNameForField(TargetMetadata.UUID))
            .append(" WHERE NT.NOTIFICATION_UUID = ?;");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String findTargetSQL() {
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
            .append(" WHERE ")
            .append(targetDataMap.getTableAlias())
            .append(".")
            .append(targetDataMap.getColumnNameForField(TargetMetadata.UUID))
            .append(" = ?");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String insertTargetSQL() {
    DataMap targetDataMap = this.targetMetadata.getDataMap();
    String sql = this.insertSQL(1, targetDataMap);
    this.logger.debug(sql);
    return sql;
  }

  private String updateTargetSQL() {
    DataMap targetDataMap = this.targetMetadata.getDataMap();

    StringBuilder sb =
        new StringBuilder()
            .append("UPDATE ")
            .append(targetDataMap.getTableName())
            .append(" SET ")
            .append(targetDataMap.getColumnNameForField(TargetMetadata.NAME))
            .append(" = ?,")
            .append(targetDataMap.getColumnNameForField(TargetMetadata.PHONE_NUMBER))
            .append(" = ?")
            .append(" WHERE ")
            .append(targetDataMap.getColumnNameForField(TargetMetadata.UUID))
            .append(" = ?");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String deleteTargetSQL() {
    DataMap targetDataMap = this.targetMetadata.getDataMap();
    String matchCriteria =
        new StringBuilder()
            .append(targetDataMap.getColumnNameForField(TargetMetadata.UUID))
            .append(" = ?")
            .toString();

    String sql = this.deleteSQL(1, targetDataMap, matchCriteria);
    this.logger.debug(sql);
    return sql;
  }

  private String disassociateFromNotificationSQL() {
    String tableName = "NOTIFICATION_TARGET";
    String matchCriteria = "TARGET_UUID = ?";
    String sql = this.deleteSQL(1, tableName, matchCriteria);
    this.logger.debug(sql);
    return sql;
  }

  private String disassociateFromAudienceSQL() {
    String tableName = "NOTIFICATION_AUDIENCE";
    String matchCriteria = "TARGET_UUID = ?";
    String sql = this.deleteSQL(1, tableName, matchCriteria);
    this.logger.debug(sql);
    return sql;
  }

  private String countTargetsSQL() {
    DataMap targetDataMap = this.targetMetadata.getDataMap();
    StringBuilder sb =
        new StringBuilder()
            .append("SELECT ")
            .append("COUNT(*) ")
            .append("FROM ")
            .append(targetDataMap.getTableName())
            .append(" AS ")
            .append(targetDataMap.getTableAlias());
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

  Target find(final UUID uuid) {

    final String targetSQL = this.findTargetSQL();

    Target target = null;
    try (PreparedStatement getTargetStatement =
        this.getUnitOfWork().createPreparedStatement(targetSQL)) {

      int index = 1;
      getTargetStatement.setString(index, uuid.toString());
      try (ResultSet targetRs = getTargetStatement.executeQuery()) {
        if (targetRs.next()) {
          target = this.targetFactory.reconstitute(targetRs);
        }
      }
      return target;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  void insert(final Target target) {

    final String insertTargetSQL = this.insertTargetSQL();

    try (PreparedStatement insertTargetStatement =
        this.getUnitOfWork().createPreparedStatement(insertTargetSQL)) {

      int index = 0;
      insertTargetStatement.setString(++index, target.getId().toString());
      insertTargetStatement.setString(++index, target.getName());
      insertTargetStatement.setString(++index, target.getPhoneNumber().toE164());
      insertTargetStatement.executeUpdate();
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  void update(final Target target) {

    final String updateTargetSQL = this.updateTargetSQL();

    try (PreparedStatement updateTargetStatement =
        this.getUnitOfWork().createPreparedStatement(updateTargetSQL)) {

      int index = 0;
      updateTargetStatement.setString(++index, target.getName());
      updateTargetStatement.setString(++index, target.getPhoneNumber().toE164());
      updateTargetStatement.setString(++index, target.getId().toString());
      updateTargetStatement.executeUpdate();
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  void delete(final UUID uuid) {

    final String deleteTargetSQL = this.deleteTargetSQL();
    final String disassociateFromNotificationSQL = this.disassociateFromNotificationSQL();
    final String disassociateFromAudienceSQL = this.disassociateFromAudienceSQL();

    try (PreparedStatement disassociateFromNotificationStatement =
            this.getUnitOfWork().createPreparedStatement(disassociateFromNotificationSQL);
        PreparedStatement disassociateFromAudienceStatement =
            this.getUnitOfWork().createPreparedStatement(disassociateFromAudienceSQL);
        PreparedStatement deleteTargetStatement =
            this.getUnitOfWork().createPreparedStatement(deleteTargetSQL)) {

      int index = 1;
      disassociateFromNotificationStatement.setString(index, uuid.toString());
      disassociateFromNotificationStatement.executeUpdate();

      disassociateFromAudienceStatement.setString(index, uuid.toString());
      disassociateFromAudienceStatement.executeUpdate();

      deleteTargetStatement.setString(index, uuid.toString());
      deleteTargetStatement.executeUpdate();
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  int count() {

    final String countTargetsSQL = this.countTargetsSQL();

    try (final PreparedStatement countTargetsStatement =
            this.getUnitOfWork().createPreparedStatement(countTargetsSQL);
        final ResultSet rs = countTargetsStatement.executeQuery()) {
      int index = 1;
      return rs.getInt(index);
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }
}
