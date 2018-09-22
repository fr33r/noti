package infrastructure;

import domain.Audience;
import domain.EntitySQLFactory;
import domain.Target;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.inject.Named;
import org.slf4j.Logger;

final class AudienceDataMapper extends DataMapper {

  private final EntitySQLFactory<Audience, UUID> audienceFactory;
  private final AudienceMetadata audienceMetadata;
  private final TargetMetadata targetMetadata;
  private final Logger logger;

  AudienceDataMapper(
      SQLUnitOfWork unitOfWork,
      EntitySQLFactory<Audience, UUID> audienceFactory,
      @Named("infrastructure.AudienceDataMapper") Logger logger) {
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

  private String updateAudienceSQL() {
    DataMap audienceDataMap = this.audienceMetadata.getDataMap();

    StringBuilder sb =
        new StringBuilder()
            .append("UPDATE ")
            .append(audienceDataMap.getTableName())
            .append(" SET ")
            .append(audienceDataMap.getColumnNameForField(AudienceMetadata.NAME))
            .append(" = ?")
            .append(" WHERE ")
            .append(audienceDataMap.getColumnNameForField(AudienceMetadata.UUID))
            .append(" = ?");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String insertAudienceSQL() {
    DataMap audienceDataMap = this.audienceMetadata.getDataMap();
    String sql = this.insertSQL(1, audienceDataMap);
    this.logger.debug(sql);
    return sql;
  }

  private String findAudienceSQL() {
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
            .append(" WHERE ")
            .append(audienceDataMap.getTableAlias())
            .append(".")
            .append(audienceDataMap.getColumnNameForField(AudienceMetadata.UUID))
            .append(" = ?");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String associateMemberSQL(int numberOfMembers) {
    String tableName = "AUDIENCE_TARGET";
    List<String> columnNames = new ArrayList<>();
    columnNames.add("AUDIENCE_UUID");
    columnNames.add("TARGET_UUID");
    String sql = this.insertSQL(numberOfMembers, tableName, columnNames);
    this.logger.debug(sql);
    return sql;
  }

  private String disassociateMemberSQL(int numberOfMembers) {
    String tableName = "AUDIENCE_TARGET";
    String matchCriteria = "AUDIENCE_UUID = ? AND TARGET_UUID = ?";
    String sql = this.deleteSQL(numberOfMembers, tableName, matchCriteria);
    this.logger.debug(sql);
    return sql;
  }

  private String deleteAudienceSQL() {
    DataMap audienceDataMap = this.audienceMetadata.getDataMap();
    String matchCriteria =
        new StringBuilder()
            .append(audienceDataMap.getColumnNameForField(AudienceMetadata.UUID))
            .append(" = ?")
            .toString();
    String sql = this.deleteSQL(1, audienceDataMap, matchCriteria);
    this.logger.debug(sql);
    return sql;
  }

  private String disassociateMembersSQL() {
    String tableName = "AUDIENCE_TARGET";
    String matchCriteria = "AUDIENCE_UUID = ?";
    String sql = this.deleteSQL(1, tableName, matchCriteria);
    this.logger.debug(sql);
    return sql;
  }

  private String countAudiencesSQL() {
    DataMap audienceDataMap = this.audienceMetadata.getDataMap();
    StringBuilder sb =
        new StringBuilder()
            .append("SELECT ")
            .append("COUNT(*) ")
            .append("FROM ")
            .append(audienceDataMap.getTableName())
            .append(" AS ")
            .append(audienceDataMap.getTableAlias());
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

  Audience find(final UUID uuid) {

    String audienceSQL = this.findAudienceSQL();
    String membersSQL = this.findAudienceMembersSQL();

    Audience audience = null;
    try (final PreparedStatement getAudienceStatement =
            this.getUnitOfWork().createPreparedStatement(audienceSQL);
        final PreparedStatement getAudienceMembersStatement =
            this.getUnitOfWork().createPreparedStatement(membersSQL)) {
      int index = 1;
      getAudienceStatement.setString(index, uuid.toString());
      getAudienceMembersStatement.setString(index, uuid.toString());

      try (final ResultSet audienceRS = getAudienceStatement.executeQuery();
          final ResultSet membersRs = getAudienceMembersStatement.executeQuery()) {
        if (audienceRS.next()) {
          audience = this.audienceFactory.reconstitute(audienceRS, membersRs);
        }
      }
      return audience;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  void update(final Audience audience) {

    String audienceSQL = this.updateAudienceSQL();

    Audience existingAudience = this.find(audience.getId());
    if (existingAudience == null) return;

    try (final PreparedStatement updateAudienceStatement =
        this.getUnitOfWork().createPreparedStatement(audienceSQL); ) {
      int index = 0;
      updateAudienceStatement.setString(++index, audience.name());
      updateAudienceStatement.setString(++index, audience.getId().toString());
      updateAudienceStatement.executeUpdate();

      Set<UUID> toAssociate = new HashSet<>();
      Set<UUID> toDisassociate = new HashSet<>();

      // determine which members are being added.
      for (Target member : audience.members()) {
        boolean exists = false;
        for (Target existingMember : existingAudience.members()) {
          if (member.getId().equals(existingMember.getId())) {
            exists = true;
            break;
          }
        }
        if (!exists) {
          toAssociate.add(member.getId());
        }
      }

      // determine which members are being removed.
      for (Target existingMember : existingAudience.members()) {
        boolean exists = false;
        for (Target member : audience.members()) {
          if (existingMember.getId().equals(member.getId())) {
            exists = true;
            break;
          }
        }
        if (!exists) {
          toDisassociate.add(existingMember.getId());
        }
      }

      String associateMemberSQL = this.associateMemberSQL(toAssociate.size());
      try (final PreparedStatement associateMemberStatement =
          this.getUnitOfWork().createPreparedStatement(associateMemberSQL)) {
        index = 0;
        for (UUID uuid : toAssociate) {
          associateMemberStatement.setString(++index, audience.getId().toString());
          associateMemberStatement.setString(++index, uuid.toString());
        }
        associateMemberStatement.executeUpdate();
      }

      String disassociateMemberSQL = this.disassociateMemberSQL(toDisassociate.size());
      try (final PreparedStatement disassociateMemberStatement =
          this.getUnitOfWork().createPreparedStatement(disassociateMemberSQL)) {
        index = 0;
        for (UUID uuid : toDisassociate) {
          disassociateMemberStatement.setString(++index, audience.getId().toString());
          disassociateMemberStatement.setString(++index, uuid.toString());
        }
        disassociateMemberStatement.executeUpdate();
      }
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  void insert(final Audience audience) {

    String audienceSQL = this.insertAudienceSQL();
    String associateMemberSQL = this.associateMemberSQL(audience.members().size());

    try (final PreparedStatement createAudienceStatement =
        this.getUnitOfWork().createPreparedStatement(audienceSQL)) {
      int index = 0;
      createAudienceStatement.setString(++index, audience.getId().toString());
      createAudienceStatement.setString(++index, audience.name());
      createAudienceStatement.executeUpdate();

      try (PreparedStatement associateMemberStatement =
          this.getUnitOfWork().createPreparedStatement(associateMemberSQL)) {
        index = 0;
        for (Target member : audience.members()) {
          associateMemberStatement.setString(++index, audience.getId().toString());
          associateMemberStatement.setString(++index, member.getId().toString());
        }
        associateMemberStatement.executeUpdate();
      }
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  void delete(final UUID uuid) {

    String disassociateMembersSQL = this.disassociateMembersSQL();
    String audienceSQL = this.deleteAudienceSQL();

    try (final PreparedStatement removeAudienceStatement =
            this.getUnitOfWork().createPreparedStatement(audienceSQL);
        final PreparedStatement disassociateMembersStatement =
            this.getUnitOfWork().createPreparedStatement(disassociateMembersSQL)) {
      int index = 1;
      disassociateMembersStatement.setString(index, uuid.toString());
      disassociateMembersStatement.executeUpdate();

      removeAudienceStatement.setString(index, uuid.toString());
      removeAudienceStatement.executeUpdate();
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  int count() {

    final String countAudiencesSQL = this.countAudiencesSQL();

    try (final PreparedStatement countAudiencesStatement =
            this.getUnitOfWork().createPreparedStatement(countAudiencesSQL);
        final ResultSet rs = countAudiencesStatement.executeQuery()) {
      int index = 1;
      return rs.getInt(index);
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }
}
