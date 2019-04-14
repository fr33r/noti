package infrastructure;

import java.sql.Types;

/**
 * Metadata defining the mappings between {@link domain.Template} and its corresponding database
 * schema.
 *
 * <p>Implementation of Metadata Mapper pattern.
 *
 * @author Jon Freer
 */
public final class TemplateMetadata extends MetadataMapper {

  /** Represents the {@link domain.Template} {@code UUID} field. */
  public static final String UUID = "uuid";

  /** Represents the {@link domain.Template} {@code content} field. */
  public static final String CONTENT = "content";

  /** Construcs new {@link TemplateMetadata}. */
  public TemplateMetadata() {
    super(new DataMap("TEMPLATE", "T"));
    this.getDataMap().addColumn("UUID", Types.VARCHAR, UUID);
    this.getDataMap().addColumn("CONTENT", Types.VARCHAR, CONTENT);
  }
}
