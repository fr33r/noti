package infrastructure;

import static org.junit.Assert.*;

import java.sql.Types;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public final class AudienceMetadataTest {

  private AudienceMetadata sut;

  @Before
  public void setup() {
    this.sut = new AudienceMetadata();
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
    String columnName = dataMap.getColumnNameForField(AudienceMetadata.UUID);
    Integer type = dataMap.getColumnTypeForColumn(expectedColumnName);
    String fieldName = dataMap.getFieldNameForColumn(expectedColumnName);

    // assert.
    assertEquals(expectedColumnName, columnName);
    assertEquals(expectedType, type);
    assertEquals(expectedFieldName, fieldName);
  }

  @Test
  public void getDataMap_outcomeIs_containsNameMapping() {

    // arrange.
    final String expectedColumnName = "name";
    final Integer expectedType = Types.VARCHAR;
    final String expectedFieldName = "name";

    // action.
    DataMap dataMap = this.sut.getDataMap();
    String columnName = dataMap.getColumnNameForField(AudienceMetadata.NAME);
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
    final String expectedTableName = "audience";

    // action.
    String tableName = this.sut.getDataMap().getTableName();

    // assert.
    assertEquals(expectedTableName, tableName);
  }

  @Test
  public void getTableAlias_outcomeIs_correctTableAlias() {

    // arrange.
    final String expectedTableAlias = "A";

    // action.
    String tableAlias = this.sut.getDataMap().getTableAlias();

    // assert.
    assertEquals(expectedTableAlias, tableAlias);
  }
}
