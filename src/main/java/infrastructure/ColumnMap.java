package infrastructure;

/**
 * A map that maintains an association between a database column and a domain object field.
 *
 * <p>This implementation is based off of the Metadata Mapping pattern.
 *
 * @author Jon Freer
 */
public class ColumnMap {

  private final String columnName;
  private final int columnType;
  private final String fieldName;

  /**
   * Constructs a new {@link ColumnMap}.
   *
   * @param columnName The database column name.
   * @param columnType The type of the database column.
   * @param fieldName The domain object field name.
   */
  public ColumnMap(String columnName, int columnType, String fieldName) {
    this.columnName = columnName;
    this.columnType = columnType;
    this.fieldName = fieldName;
  }

  /**
   * Retrieves the database column name.
   *
   * @return The database column name.
   */
  public String getColumnName() {
    return this.columnName;
  }

  /**
   * Retrieves the domain object field name.
   *
   * @return The domain object field name.
   */
  public String getFieldName() {
    return this.fieldName;
  }

  /**
   * Retrieves the database column type.
   *
   * @return The database column type.
   */
  public int getColumnType() {
    return this.columnType;
  }
}
