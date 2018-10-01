package infrastructure;

import java.sql.Types;

public final class MessageMetadata extends MetadataMapper {

  public static final String ID = "id";
  public static final String FROM = "from";
  public static final String TO = "to";
  public static final String CONTENT = "content";
  public static final String STATUS = "status";
  public static final String EXTERNAL_ID = "externalID";

  public MessageMetadata() {
    super(new DataMap("MESSAGE", "M"));
    this.getDataMap().addColumn("ID", Types.BIGINT, ID);
    this.getDataMap().addColumn("`FROM`", Types.VARCHAR, FROM);
    this.getDataMap().addColumn("`TO`", Types.VARCHAR, TO);
    this.getDataMap().addColumn("CONTENT", Types.VARCHAR, CONTENT);
    this.getDataMap().addColumn("STATUS", Types.VARCHAR, STATUS);
    this.getDataMap().addColumn("EXTERNAL_ID", Types.VARCHAR, EXTERNAL_ID);
  }
}
