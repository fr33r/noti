package infrastructure;

import java.sql.Types;

/**
 * Metadata defining the mappings between {@link Notification} and its corresponding database
 * schema.
 *
 * <p>Implementation of Metadata Mapper pattern.
 *
 * @author Jon Freer
 */
public final class NotificationMetadata extends MetadataMapper {

  public static final String UUID = "uuid";
  public static final String CONTENT = "content";
  public static final String SEND_AT = "sendAt";
  public static final String SENT_AT = "sentAt";
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
