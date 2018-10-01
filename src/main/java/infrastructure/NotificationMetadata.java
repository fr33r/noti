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

  /** Represents the {@link domain.Notification} {@code UUID} field. */
  public static final String UUID = "uuid";

  /** Represents the {@link domain.Notification} {@code content} field. */
  public static final String CONTENT = "content";

  /** Represents the {@link domain.Notification} {@code sendAt} field. */
  public static final String SEND_AT = "sendAt";

  /** Represents the {@link domain.Notification} {@code sentAt} field. */
  public static final String SENT_AT = "sentAt";

  /** Represents the {@link domain.Notification} {@code status} field. */
  public static final String STATUS = "status";

  /** Constructs new {@link NotificationMetadata}. */
  public NotificationMetadata() {
    super(new DataMap("NOTIFICATION", "N"));
    this.getDataMap().addColumn("UUID", Types.VARCHAR, UUID);
    this.getDataMap().addColumn("MESSAGE", Types.VARCHAR, CONTENT);
    this.getDataMap().addColumn("SEND_AT", Types.TIMESTAMP, SEND_AT);
    this.getDataMap().addColumn("SENT_AT", Types.TIMESTAMP, SENT_AT);
    this.getDataMap().addColumn("STATUS", Types.VARCHAR, STATUS);
  }
}
