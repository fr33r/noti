package domain;

import infrastructure.AudienceMetadata;
import infrastructure.DataMap;
import infrastructure.MessageMetadata;
import infrastructure.NotificationMetadata;
import infrastructure.TargetMetadata;
import io.opentracing.Tracer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import org.jvnet.hk2.annotations.Service;

/**
 * NOT WHAT ENVISION AS THE RIGHT SOLUTION. SEE PAGE 143 of DDD. THIS FACTORY IS NOT USING
 * ABSTRACTIONS AS RETURN TYPES. COULDNT THINK OF A WAY AROUND THIS, SINCE CLIENTS OF THIS FACTORY
 * MUST HAVE CONCRETE NOTIFICATION CLASS (NOTIFICATIONREPOSITORY FOR EXAMPLE).
 */
@Service
@Named("NotificationSQLFactory")
public class NotificationSQLFactory extends EntitySQLFactory<Notification, UUID> {

  private static final String targetUUIDColumn = "target_uuid";
  private static final String audienceUUIDColumn = "audience_uuid";
  private static final Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

  private final Tracer tracer;
  private final NotificationMetadata notificationMedadata;
  private final AudienceMetadata audienceMetadata;
  private final TargetMetadata targetMetadata;
  private final MessageMetadata messageMetadata;

  @Inject
  public NotificationSQLFactory(Tracer tracer) {
    // TODO - pass these in via DI.
    this.notificationMedadata = new NotificationMetadata();
    this.audienceMetadata = new AudienceMetadata();
    this.targetMetadata = new TargetMetadata();
    this.messageMetadata = new MessageMetadata();
    this.tracer = tracer;
  }

  public Notification reconstitute(Statement statement) {

    if (statement == null) {
      return null;
    }

    Notification notification = null;
    Set<Target> targets = null;
    try {
      if (statement.isClosed()) {
        return null;
      }

      notification = this.extractNotification(statement.getResultSet());
      if (statement.getMoreResults()) {
        targets = this.extractTargets(statement.getResultSet());
        for (Target target : targets) {
          notification.includeRecipient(target);
        }
      }
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
    return notification;
  }

  private Map<UUID, Set<Target>> extractMembers(ResultSet results) throws SQLException {
    Map<UUID, Set<Target>> membersForAudiences = new HashMap<>();
    while (results.next()) {
      String audienceUUID = results.getString(audienceUUIDColumn);
      Target member = this.extractTarget(results);
      if (!membersForAudiences.containsKey(audienceUUID)) {
        Set<Target> members = new HashSet<>();
        members.add(member);
        membersForAudiences.put(UUID.fromString(audienceUUID), members);
      } else {
        membersForAudiences.get(UUID.fromString(audienceUUID)).add(member);
      }
    }
    return membersForAudiences;
  }

  private Target extractTarget(ResultSet results) throws SQLException {
    DataMap targetDataMap = this.targetMetadata.getDataMap();
    String uuidColumn = targetDataMap.getColumnNameForField(TargetMetadata.UUID);
    String nameColumn = targetDataMap.getColumnNameForField(TargetMetadata.NAME);
    String phoneNumberColumn = targetDataMap.getColumnNameForField(TargetMetadata.PHONE_NUMBER);

    String uuid = results.getString(uuidColumn);
    String name = results.getString(nameColumn);
    String phoneNumber = results.getString(phoneNumberColumn);
    return new Target(new Target(UUID.fromString(uuid), name, new PhoneNumber(phoneNumber)));
  }

  private Set<Target> extractTargets(ResultSet results) throws SQLException {
    Set<Target> targets = new HashSet<>();
    while (results.next()) {
      targets.add(this.extractTarget(results));
    }
    return targets;
  }

  private Notification extractNotification(ResultSet results) throws SQLException {
    DataMap notificationDataMap = this.notificationMedadata.getDataMap();
    String uuidColumn = notificationDataMap.getColumnNameForField(NotificationMetadata.UUID);
    String contentColumn = notificationDataMap.getColumnNameForField(NotificationMetadata.CONTENT);
    String sentAtColumn = notificationDataMap.getColumnNameForField(NotificationMetadata.SENT_AT);
    String statusColumn = notificationDataMap.getColumnNameForField(NotificationMetadata.STATUS);
    String sendAtColumn = notificationDataMap.getColumnNameForField(NotificationMetadata.SEND_AT);

    String uuid = results.getString(uuidColumn);
    String content = results.getString(contentColumn);
    Timestamp sentAtTimestamp = results.getTimestamp(sentAtColumn, utc);
    Date sentAt = null;
    if (sentAtTimestamp != null) {
      sentAt = new Date(sentAtTimestamp.getTime());
    }
    String status = results.getString(statusColumn);
    Timestamp sendAtTimestamp = results.getTimestamp(sendAtColumn, utc);
    Date sendAt = null;
    if (sendAtTimestamp != null) {
      sendAt = new Date(sendAtTimestamp.getTime());
    }

    // compare persisted state with computed state. log error and adopt computed.
    NotificationStatus statusEnum = NotificationStatus.valueOf(status);

    NotificationBuilder builder = new NotificationBuilder();
    return builder.identity(uuid).content(content).sendAt(sendAt).sentAt(sentAt).build();
  }

  private Set<Message> extractMessages(ResultSet results) throws SQLException {
    DataMap messageDataMap = this.messageMetadata.getDataMap();
    String idColumn = messageDataMap.getColumnNameForField(MessageMetadata.ID);
    String fromColumn = messageDataMap.getColumnNameForField(MessageMetadata.FROM);
    String toColumn = messageDataMap.getColumnNameForField(MessageMetadata.TO);
    String contentColumn = messageDataMap.getColumnNameForField(MessageMetadata.CONTENT);
    String statusColumn = messageDataMap.getColumnNameForField(MessageMetadata.STATUS);
    String externalIdColumn = messageDataMap.getColumnNameForField(MessageMetadata.EXTERNAL_ID);

    Set<Message> messages = new HashSet<Message>();
    while (results.next()) {
      Integer id = results.getInt(idColumn);
      String from = results.getString(fromColumn);
      String to = results.getString(toColumn);
      String content = results.getString(contentColumn);
      String status = results.getString(statusColumn);
      String externalId = results.getString(externalIdColumn);
      messages.add(
          new Message(
              id,
              new PhoneNumber(from),
              new PhoneNumber(to),
              content,
              MessageStatus.valueOf(status),
              externalId));
    }

    return messages;
  }

  private Set<Audience> extractAudiences(ResultSet results) throws SQLException {
    DataMap audienceDataMap = this.audienceMetadata.getDataMap();
    String uuidColumn = audienceDataMap.getColumnNameForField(AudienceMetadata.UUID);
    String nameColumn = audienceDataMap.getColumnNameForField(AudienceMetadata.NAME);

    Set<Audience> audiences = new HashSet<>();
    while (results.next()) {
      String uuid = results.getString(uuidColumn);
      String name = results.getString(nameColumn);
      audiences.add(new Audience(UUID.fromString(uuid), name, new HashSet<>()));
    }
    return audiences;
  }

  @Override
  public Notification reconstitute(ResultSet... results) {

    if (results == null || results.length < 1) {
      return null;
    }

    Notification notification = null;
    Set<Target> targets = null;
    Set<Message> messages = null;
    Set<Audience> audiences = null;
    Map<UUID, Set<Target>> members = null;

    try {

      notification = this.extractNotification(results[0]);
      if (results.length > 1) {
        targets = this.extractTargets(results[1]);
        for (Target target : targets) {
          notification.includeRecipient(target);
        }
      }

      if (results.length > 2) {
        messages = this.extractMessages(results[2]);
        notification.messages(messages);
      }

      if (results.length > 3) {
        audiences = this.extractAudiences(results[3]);
        notification.audiences(audiences);
      }

      if (results.length > 4) {
        members = this.extractMembers(results[4]);
        for (Audience audience : audiences) {
          if (members.containsKey(audience.getId())) {
            for (Target member : members.get(audience.getId())) {
              audience.include(member);
            }
          }
        }
      }

    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
    return notification;
  }
}
