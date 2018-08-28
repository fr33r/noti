package infrastructure;

import java.sql.Types;

/**
 * Metadata defining the mappings between {@link domain.Notification} and its corresponding database
 * schema.
 *
 * <p>Implementation of Metadata Mapper pattern.
 *
 * @author Jon Freer
 */
public final class NotificationMetadata extends MetadataMapper {

  /** Represents the {@link Notification} {@code UUID} field. */
  public static final String UUID = "uuid";

  /** Represents the {@link Notification} {@code content} field. */
  public static final String CONTENT = "content";

  /** Represents the {@link Notification} {@code sendAt} field. */
  public static final String SEND_AT = "sendAt";

  /** Represents the {@link Notification} {@code sentAt} field. */
  public static final String SENT_AT = "sentAt";

  /** Represents the {@link Notification} {@code status} field. */
  public static final String STATUS = "status";

  /** Constructs new {@link NotificationMetadata}. */
  public NotificationMetadata() {
    super(new DataMap("notification", "N"));
    this.getDataMap().addColumn("uuid", Types.VARCHAR, UUID);
    this.getDataMap().addColumn("message", Types.VARCHAR, CONTENT);
    this.getDataMap().addColumn("send_at", Types.TIMESTAMP, SEND_AT);
    this.getDataMap().addColumn("sent_at", Types.TIMESTAMP, SENT_AT);
    this.getDataMap().addColumn("status", Types.VARCHAR, STATUS);
  }
}
