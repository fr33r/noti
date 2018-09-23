package domain;

import infrastructure.AudienceMetadata;
import infrastructure.DataMap;
import infrastructure.TargetMetadata;
import io.opentracing.Tracer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;

public class AudienceSQLFactory extends EntitySQLFactory<Audience, UUID> {

  private final AudienceMetadata audienceMetadata;
  private final TargetMetadata targetMetadata;
  private final Tracer tracer;

  @Inject
  public AudienceSQLFactory(Tracer tracer) {
    this.audienceMetadata = new AudienceMetadata();
    this.targetMetadata = new TargetMetadata();
    this.tracer = tracer;
  }

  public Audience reconstitute(Statement statement) {
    Audience audience = null;

    try {
      // while there are still results to process.
      int resultSetIndex = 1;
      while (statement.getMoreResults() || statement.getUpdateCount() != -1) {
        if (resultSetIndex == 1) {
          // extract audience.
          audience = this.extractAudience(statement.getResultSet());
          if (audience == null) break;
        } else if (resultSetIndex == 2) {
          // extract members.
          Set<Target> members = this.extractMembers(statement.getResultSet());
          for (Target member : members) {
            audience.include(member);
          }
        } else {
          break;
        }
      }
      return audience;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  public Audience reconstitute(ResultSet... results) {
    if (results == null || results.length < 1) {
      return null;
    }

    Audience audience = null;
    try {
      audience = this.extractAudience(results[0]);

      if (results.length > 1) {
        Set<Target> members = this.extractMembers(results[1]);
        for (Target member : members) {
          audience.include(member);
        }
      }
      return audience;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  private Audience extractAudience(ResultSet results) throws SQLException {
    DataMap audienceDataMap = this.audienceMetadata.getDataMap();
    String uuidColumn = audienceDataMap.getColumnNameForField(AudienceMetadata.UUID);
    String nameColumn = audienceDataMap.getColumnNameForField(AudienceMetadata.NAME);

    String uuid = results.getString(uuidColumn);
    String name = results.getString(nameColumn);
    return new Audience(UUID.fromString(uuid), name, new HashSet<>());
  }

  private Set<Target> extractMembers(ResultSet results) throws SQLException {
    DataMap targetDataMap = this.targetMetadata.getDataMap();
    String uuidColumn = targetDataMap.getColumnNameForField(TargetMetadata.UUID);
    String nameColumn = targetDataMap.getColumnNameForField(TargetMetadata.NAME);
    String phoneNumberColumn = targetDataMap.getColumnNameForField(TargetMetadata.PHONE_NUMBER);

    Set<Target> members = new HashSet<>();
    while (results.next()) {
      String uuid = results.getString(uuidColumn);
      String name = results.getString(nameColumn);
      String phoneNumber = results.getString(phoneNumberColumn);
      members.add(new Target(UUID.fromString(uuid), name, new PhoneNumber(phoneNumber)));
    }
    return members;
  }
}
