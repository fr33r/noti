package infrastructure;

import java.sql.Types;

/**
 * Metadata defining the mappings between {@link domain.Target} and its corresponding database
 * schema.
 *
 * <p>Implementation of Metadata Mapper pattern.
 *
 * @author Jon Freer
 */
public final class TargetMetadata extends MetadataMapper {

  /** Represents the {@link domain.Target} {@code UUID} field. */
  public static final String UUID = "uuid";

  /** Represents the {@link domain.Target} {@code phone number} field. */
  public static final String PHONE_NUMBER = "phoneNumber";

  /** Represents the {@link domain.Target} {@code name} field. */
  public static final String NAME = "name";

  /** Construcs new {@link TargetMetadata}. */
  public TargetMetadata() {
    super(new DataMap("target", "T"));
    this.getDataMap().addColumn("uuid", Types.VARCHAR, UUID);
    this.getDataMap().addColumn("phone_number", Types.VARCHAR, PHONE_NUMBER);
    this.getDataMap().addColumn("name", Types.VARCHAR, NAME);
  }
}
