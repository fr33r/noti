package infrastructure;

import java.util.ArrayList;
import java.util.List;

/**
 * A map that maintains associations between a single relational database table and a set of column
 * mappings.
 *
 * <p>This implementation is based off of the Metadata Mapping pattern.
 *
 * @author Jon Freer
 */
public class DataMap {

  private final String tableName;
  private String tableAlias;
  private final List<ColumnMap> columnMap;

  /**
   * Constructs a new {@link DataMap}.
   *
   * @param tableName The name of the database table that this {@link DataMap} manages.
   */
  public DataMap(String tableName) {
    this.tableName = tableName;
    this.columnMap = new ArrayList<>();
  }

  /**
   * Constructs a new {@link DataMap}.
   *
   * @param tableName The name of the database table that this {@link DataMap} manages.
   * @param tableAlias The alias of the database table that this {@link DataMap} manages.
   */
  public DataMap(String tableName, String tableAlias) {
    this(tableName);
    this.tableAlias = tableAlias;
  }

  /**
   * Adds a column mapping to the {@link DataMap}.
   *
   * @param columnName The name of the database column.
   * @param columnType The type of the database column.
   * @param fieldName The name of the domain object field.
   */
  public void addColumn(String columnName, int columnType, String fieldName) {
    this.columnMap.add(new ColumnMap(columnName, columnType, fieldName));
  }

  /**
   * Retrieves all of the column names recorded within the {@link DataMap}.
   *
   * @return A list of database column names.
   */
  public List<String> getAllColumnNames() {
    List<String> columnNames = new ArrayList<>();
    for (ColumnMap map : this.columnMap) {
      columnNames.add(map.getColumnName());
    }
    return columnNames;
  }

  public List<String> getAllColumnNamesWithAliases() {
    List<String> columnNames = new ArrayList<>();
    for (ColumnMap map : this.columnMap) {
      String columnName = String.format("%s.%s", this.getTableAlias(), map.getColumnName());
      columnNames.add(columnName);
    }
    return columnNames;
  }

  /**
   * Retrieves the corresponding domain object field name for the given database column name.
   *
   * @param columnName The name of the database column.
   * @return The corresponding domain object field name.
   */
  public String getFieldNameForColumn(String columnName) {
    for (ColumnMap map : this.columnMap) {
      if (map.getColumnName().equals(columnName)) {
        return map.getFieldName();
      }
    }
    return null;
  }

  /**
   * Retrieves the corresponding database column name for the given domain object field name.
   *
   * @param fieldName The domain object field name.
   * @return The name of the database column.
   */
  public String getColumnNameForField(String fieldName) {
    for (ColumnMap map : this.columnMap) {
      if (map.getFieldName().equals(fieldName)) {
        return map.getColumnName();
      }
    }
    return null;
  }

  public Integer getColumnTypeForColumn(String columnName) {
    for (ColumnMap map : this.columnMap) {
      if (map.getColumnName().equals(columnName)) {
        return map.getColumnType();
      }
    }
    return null;
  }

  /**
   * Retrieves the database table name.
   *
   * @return The database table name.
   */
  public String getTableName() {
    return this.tableName;
  }

  /**
   * Retrieves the database table alias. If one was not specified upon construction of the {@link
   * DataMap}, one is generated.
   *
   * @return The database table alias.
   */
  public String getTableAlias() {
    if (this.tableAlias == null) {
      this.tableAlias = this.generateTableAlias();
    }
    return this.tableAlias;
  }

  /**
   * Generates a database table alias.
   *
   * @return The generated table alias.
   */
  private String generateTableAlias() {
    String[] parts = this.tableName.split("_");
    StringBuilder sb = new StringBuilder();
    for (String part : parts) {
      sb.append(part.substring(0, 1).toUpperCase());
    }
    return sb.toString();
  }
}
