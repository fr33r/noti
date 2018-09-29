package infrastructure;

import static org.junit.Assert.*;

import java.sql.Types;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public final class MessageMetadataTest {

  private MessageMetadata sut;

  @Before
  public void setup() {
    this.sut = new MessageMetadata();
  }

  @After
  public void tearDown() {
    this.sut = null;
  }

  @Test
  public void getDataMap_outcomeIs_containsIDMapping() {

    // arrange.
    final String expectedColumnName = "id";
    final Integer expectedType = Types.BIGINT;
    final String expectedFieldName = "id";

    // action.
    DataMap dataMap = this.sut.getDataMap();
    String columnName = dataMap.getColumnNameForField(MessageMetadata.ID);
    Integer type = dataMap.getColumnTypeForColumn(expectedColumnName);
    String fieldName = dataMap.getFieldNameForColumn(expectedColumnName);

    // assert.
    assertEquals(expectedColumnName, columnName);
    assertEquals(expectedType, type);
    assertEquals(expectedFieldName, fieldName);
  }

  @Test
  public void getDataMap_outcomeIs_containsContentMapping() {

    // arrange.
    final String expectedColumnName = "content";
    final Integer expectedType = Types.VARCHAR;
    final String expectedFieldName = "content";

    // action.
    DataMap dataMap = this.sut.getDataMap();
    String columnName = dataMap.getColumnNameForField(MessageMetadata.CONTENT);
    Integer type = dataMap.getColumnTypeForColumn(expectedColumnName);
    String fieldName = dataMap.getFieldNameForColumn(expectedColumnName);

    // assert.
    assertEquals(expectedColumnName, columnName);
    assertEquals(expectedType, type);
    assertEquals(expectedFieldName, fieldName);
  }

  @Test
  public void getDataMap_outcomeIs_containsStatusMapping() {

    // arrange.
    final String expectedColumnName = "status";
    final Integer expectedType = Types.VARCHAR;
    final String expectedFieldName = "status";

    // action.
    DataMap dataMap = this.sut.getDataMap();
    String columnName = dataMap.getColumnNameForField(MessageMetadata.STATUS);
    Integer type = dataMap.getColumnTypeForColumn(expectedColumnName);
    String fieldName = dataMap.getFieldNameForColumn(expectedColumnName);

    // assert.
    assertEquals(expectedColumnName, columnName);
    assertEquals(expectedType, type);
    assertEquals(expectedFieldName, fieldName);
  }

  @Test
  public void getDataMap_outcomeIs_containsFromMapping() {

    // arrange.
    final String expectedColumnName = "`from`";
    final Integer expectedType = Types.VARCHAR;
    final String expectedFieldName = "from";

    // action.
    DataMap dataMap = this.sut.getDataMap();
    String columnName = dataMap.getColumnNameForField(MessageMetadata.FROM);
    Integer type = dataMap.getColumnTypeForColumn(expectedColumnName);
    String fieldName = dataMap.getFieldNameForColumn(expectedColumnName);

    // assert.
    assertEquals(expectedColumnName, columnName);
    assertEquals(expectedType, type);
    assertEquals(expectedFieldName, fieldName);
  }

  @Test
  public void getDataMap_outcomeIs_containsToMapping() {

    // arrange.
    final String expectedColumnName = "`to`";
    final Integer expectedType = Types.VARCHAR;
    final String expectedFieldName = "to";

    // action.
    DataMap dataMap = this.sut.getDataMap();
    String columnName = dataMap.getColumnNameForField(MessageMetadata.TO);
    Integer type = dataMap.getColumnTypeForColumn(expectedColumnName);
    String fieldName = dataMap.getFieldNameForColumn(expectedColumnName);

    // assert.
    assertEquals(expectedColumnName, columnName);
    assertEquals(expectedType, type);
    assertEquals(expectedFieldName, fieldName);
  }

  @Test
  public void getDataMap_outcomeIs_containsExternalIDMapping() {

    // arrange.
    final String expectedColumnName = "external_id";
    final Integer expectedType = Types.VARCHAR;
    final String expectedFieldName = "externalID";

    // action.
    DataMap dataMap = this.sut.getDataMap();
    String columnName = dataMap.getColumnNameForField(MessageMetadata.EXTERNAL_ID);
    Integer type = dataMap.getColumnTypeForColumn(expectedColumnName);
    String fieldName = dataMap.getFieldNameForColumn(expectedColumnName);

    // assert.
    assertEquals(expectedColumnName, columnName);
    assertEquals(expectedType, type);
    assertEquals(expectedFieldName, fieldName);
  }

  @Test
  public void getTableName_outcomeIs_correctTableName() {

    // arrange.
    final String expectedTableName = "message";

    // action.
    String tableName = this.sut.getDataMap().getTableName();

    // assert.
    assertEquals(expectedTableName, tableName);
  }

  @Test
  public void getTableAlias_outcomeIs_correctTableAlias() {

    // arrange.
    final String expectedTableAlias = "M";

    // action.
    String tableAlias = this.sut.getDataMap().getTableAlias();

    // assert.
    assertEquals(expectedTableAlias, tableAlias);
  }
}
