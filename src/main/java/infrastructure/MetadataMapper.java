package infrastructure;

/**
 * Represents the abstraction that contains the mappings between a domain object and its
 * corresponding database schema.
 *
 * <p>Implementation of Metadata Mapper pattern.
 *
 * @author Jon Freer
 */
public class MetadataMapper {

  private final DataMap dataMap;

  /**
   * Construcs a new {@link MetadataMapper}.
   *
   * @param dataMap The {@link DataMap} containing the mappings between the domain object and the
   *     database schema.
   */
  public MetadataMapper(DataMap dataMap) {
    this.dataMap = dataMap;
  }

  /**
   * Retrieves the {@link DataMap}.
   *
   * @return The {@link DataMap}.
   */
  public DataMap getDataMap() {
    return this.dataMap;
  }
}
