package infrastructure;

import java.sql.Types;

/**
 * Metadata defining the mappings between {@link domain.Audience} and its corresponding database
 * schema.
 *
 * <p>Implementation of Metadata Mapper pattern.
 *
 * @author Jon Freer
 */
public final class AudienceMetadata extends MetadataMapper {

  /** Represents the {@link domain.Audience} {@code UUID} field. */
  public static final String UUID = "uuid";

  /** Represents the {@link domain.Audience} {@code name} field. */
  public static final String NAME = "name";

  /** Construcs new {@link AudienceMetadata}. */
  public AudienceMetadata() {
    super(new DataMap("audience", "A"));
    this.getDataMap().addColumn("uuid", Types.VARCHAR, UUID);
    this.getDataMap().addColumn("name", Types.VARCHAR, NAME);
  }
}
