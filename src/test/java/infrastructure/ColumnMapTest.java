package infrastructure;

import static org.junit.Assert.*;

import java.sql.Types;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public final class ColumnMapTest {

  private String columnName;
  private Integer columnType;
  private String fieldName;
  private ColumnMap sut;

  @Before
  public void setup() {
    this.columnName = "uuid";
    this.columnType = Types.VARCHAR;
    this.fieldName = "uuid";
    this.sut = new ColumnMap(this.columnName, this.columnType, this.fieldName);
  }

  @After
  public void tearDown() {
    this.columnName = null;
    this.columnType = null;
    this.fieldName = null;
    this.sut = null;
  }

  @Test
  public void getColumnName_outcomeIs_correctColumnName() {

    // arrange.
    String expectedColumnName = "uuid";

    // action.
    String columnName = this.sut.getColumnName();

    // assert.
    assertEquals(expectedColumnName, columnName);
  }

  @Test
  public void getFieldName_outcomeIs_correctFieldName() {

    // arrange.
    String expectedFieldName = "uuid";

    // action.
    String fieldName = this.sut.getFieldName();

    // assert.
    assertEquals(expectedFieldName, fieldName);
  }

  @Test
  public void getColumnType_outcomeIs_correctedColumnType() {

    // arrange.
    Integer expectedColumnType = Types.VARCHAR;

    // action.
    Integer columnType = this.sut.getColumnType();

    // assert.
    assertEquals(expectedColumnType, columnType);
  }
}
