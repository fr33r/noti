package infrastructure;

import static org.junit.Assert.*;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public final class DataMapTest {

  private DataMap sut;
  private String tableName;
  private String tableAlias;

  @Before
  public void setup() {
    this.tableName = "test";
    this.tableAlias = "T";
    this.sut = new DataMap(this.tableName, this.tableAlias);
  }

  @After
  public void tearDown() {
    this.tableName = null;
    this.tableAlias = null;
    this.sut = null;
  }

  @Test
  public void getAllColumnNames_outcomeIs_correctColumnNames() {

    // arrange.
    String columnName, fieldName;
    Integer columnType;
    List<String> expectedColumnNames = new ArrayList<>();
    columnName = "uuid";
    columnType = Types.VARCHAR;
    fieldName = "uuid";
    expectedColumnNames.add(columnName);
    this.sut.addColumn(columnName, columnType, fieldName);
    columnName = "name";
    fieldName = "name";
    expectedColumnNames.add(columnName);
    this.sut.addColumn(columnName, columnType, fieldName);

    // action.
    List<String> columnNames = this.sut.getAllColumnNames();

    // assert.
    assertEquals(expectedColumnNames, columnNames);
  }

  @Test
  public void getAllColumnNamesWithAliases_outcomeIs_correctTableNamesWithAliases() {

    // arrange.
    String columnName, columnNameWithAlias, fieldName;
    Integer columnType;
    List<String> expectedColumnNamesWithAliases = new ArrayList<>();
    columnName = "uuid";
    columnNameWithAlias = String.format("%s.%s", this.tableAlias, columnName);
    columnType = Types.VARCHAR;
    fieldName = "uuid";
    expectedColumnNamesWithAliases.add(columnNameWithAlias);
    this.sut.addColumn(columnName, columnType, fieldName);
    columnName = "name";
    columnNameWithAlias = String.format("%s.%s", this.tableAlias, columnName);
    fieldName = "name";
    expectedColumnNamesWithAliases.add(columnNameWithAlias);
    this.sut.addColumn(columnName, columnType, fieldName);

    // action.
    List<String> columnNamesWithAliases = this.sut.getAllColumnNamesWithAliases();

    // assert.
    assertEquals(expectedColumnNamesWithAliases, columnNamesWithAliases);
  }

  @Test
  public void getFieldNameForColumn_outcomeIs_correctFieldName() {

    // arrange.
    String columnName = "uuid", expectedFieldName = "uuid";
    Integer columnType = Types.VARCHAR;
    this.sut.addColumn(columnName, columnType, expectedFieldName);

    // action.
    String fieldName = this.sut.getFieldNameForColumn(columnName);

    // assert.
    assertEquals(expectedFieldName, fieldName);
  }

  @Test
  public void getFieldNameForColumn_outcomeIs_missingFieldName() {

    // arrange.
    String columnName = "uuid", fieldName = "uuid";
    Integer columnType = Types.VARCHAR;
    this.sut.addColumn(columnName, columnType, fieldName);

    // action.
    fieldName = this.sut.getFieldNameForColumn("name");

    // assert.
    assertNull(fieldName);
  }

  @Test
  public void getColumnNameForField_outcomeIs_correctColumnName() {

    // arrange.
    String expectedColumnName = "uuid", fieldName = "uuid";
    Integer columnType = Types.VARCHAR;
    this.sut.addColumn(expectedColumnName, columnType, fieldName);

    // action.
    String columnName = this.sut.getColumnNameForField(fieldName);

    // assert.
    assertEquals(expectedColumnName, columnName);
  }

  @Test
  public void getColumnNameForField_outcomeIs_missingColumnName() {

    // arrange.
    String columnName = "uuid", fieldName = "uuid";
    Integer columnType = Types.VARCHAR;
    this.sut.addColumn(columnName, columnType, fieldName);

    // action.
    columnName = this.sut.getColumnNameForField("name");

    // assert.
    assertNull(columnName);
  }

  @Test
  public void getColumnTypeForColumn_outcomeIs_correctColumnType() {

    // arrange.
    String columnName = "uuid", fieldName = "uuid";
    Integer expectedColumnType = Types.VARCHAR;
    this.sut.addColumn(columnName, expectedColumnType, fieldName);

    // action.
    Integer columnType = this.sut.getColumnTypeForColumn(columnName);

    // assert.
    assertEquals(expectedColumnType, columnType);
  }

  @Test
  public void getColumnTypeForColumn_outcomeIs_missingColumnType() {

    // arrange.
    String columnName = "uuid", fieldName = "uuid";
    Integer columnType = Types.VARCHAR;
    this.sut.addColumn(columnName, columnType, fieldName);

    // action.
    columnType = this.sut.getColumnTypeForColumn("name");

    // assert.
    assertNull(columnType);
  }

  @Test
  public void getTableAlias_outcomeIs_correctTableAlias() {

    // arrange.
    final String expectedTableAlias = this.tableAlias;

    // action.
    String tableAlias = this.sut.getTableAlias();

    // assert.
    assertEquals(expectedTableAlias, tableAlias);
  }

  @Test
  public void getTableAlias_outcomeIs_correctGeneratedTableAlias() {

    // arrange.
    final String expectedTableAlias = "T";
    this.sut = new DataMap(this.tableName);

    // action.
    String tableAlias = this.sut.getTableAlias();

    // assert.
    assertEquals(expectedTableAlias, tableAlias);
  }

  @Test
  public void getTableName_outcomeIs_correctTableName() {

    // arrange.
    final String expectedTableName = "test";
    this.sut = new DataMap(this.tableName);

    // action.
    String tableName = this.sut.getTableName();

    // assert.
    assertEquals(expectedTableName, tableName);
  }
}
