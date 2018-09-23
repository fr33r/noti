package infrastructure;

import static org.junit.Assert.*;

import java.sql.Types;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public final class NotificationMetadataTest {

  private NotificationMetadata sut;

  @Before
  public void setup() {
    this.sut = new NotificationMetadata();
  }

  @After
  public void tearDown() {
    this.sut = null;
  }

  @Test
  public void getDataMap_outcomeIs_containsUUIDMapping() {

    // arrange.
    final String expectedColumnName = "uuid";
    final Integer expectedType = Types.VARCHAR;
    final String expectedFieldName = "uuid";

    // action.
    DataMap dataMap = this.sut.getDataMap();
    String columnName = dataMap.getColumnNameForField(NotificationMetadata.UUID);
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
    final String expectedColumnName = "message";
    final Integer expectedType = Types.VARCHAR;
    final String expectedFieldName = "content";

    // action.
    DataMap dataMap = this.sut.getDataMap();
    String columnName = dataMap.getColumnNameForField(NotificationMetadata.CONTENT);
    Integer type = dataMap.getColumnTypeForColumn(expectedColumnName);
    String fieldName = dataMap.getFieldNameForColumn(expectedColumnName);

    // assert.
    assertEquals(expectedColumnName, columnName);
    assertEquals(expectedType, type);
    assertEquals(expectedFieldName, fieldName);
  }

  @Test
  public void getDataMap_outcomeIs_containsSendAtMapping() {

    // arrange.
    final String expectedColumnName = "send_at";
    final Integer expectedType = Types.TIMESTAMP;
    final String expectedFieldName = "sendAt";

    // action.
    DataMap dataMap = this.sut.getDataMap();
    String columnName = dataMap.getColumnNameForField(NotificationMetadata.SEND_AT);
    Integer type = dataMap.getColumnTypeForColumn(expectedColumnName);
    String fieldName = dataMap.getFieldNameForColumn(expectedColumnName);

    // assert.
    assertEquals(expectedColumnName, columnName);
    assertEquals(expectedType, type);
    assertEquals(expectedFieldName, fieldName);
  }

  @Test
  public void getDataMap_outcomeIs_containsSentAtMapping() {

    // arrange.
    final String expectedColumnName = "sent_at";
    final Integer expectedType = Types.TIMESTAMP;
    final String expectedFieldName = "sentAt";

    // action.
    DataMap dataMap = this.sut.getDataMap();
    String columnName = dataMap.getColumnNameForField(NotificationMetadata.SENT_AT);
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
    String columnName = dataMap.getColumnNameForField(NotificationMetadata.STATUS);
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
    final String expectedTableName = "notification";

    // action.
    String tableName = this.sut.getDataMap().getTableName();

    // assert.
    assertEquals(expectedTableName, tableName);
  }

  @Test
  public void getTableAlias_outcomeIs_correctTableAlias() {

    // arrange.
    final String expectedTableAlias = "N";

    // action.
    String tableAlias = this.sut.getDataMap().getTableAlias();

    // assert.
    assertEquals(expectedTableAlias, tableAlias);
  }
}
