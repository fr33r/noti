package infrastructure;

import java.sql.Types;

/**
 * Metadata defining the mappings between {@link Target} and its corresponding database schema.
 *
 * <p>Implementation of Metadata Mapper pattern.
 *
 * @author Jon Freer
 */
public final class TargetMetadata extends MetadataMapper {

  public static final String UUID = "uuid";
  public static final String PHONE_NUMBER = "phoneNumber";
  public static final String NAME = "name";

  /** Construcs new {@link TargetMetadata}. */
  public TargetMetadata() {
    super(new DataMap("target", "T"));
    this.getDataMap().addColumn("uuid", Types.VARCHAR, UUID);
    this.getDataMap().addColumn("phone_number", Types.VARCHAR, PHONE_NUMBER);
    this.getDataMap().addColumn("name", Types.VARCHAR, NAME);
  }
}
