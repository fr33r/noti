package infrastructure;

import java.util.ArrayList;
import java.util.List;

class DataMapper {

  private final SQLUnitOfWork unitOfWork;

  DataMapper(SQLUnitOfWork unitOfWork) {
    this.unitOfWork = unitOfWork;
  }

  SQLUnitOfWork getUnitOfWork() {
    return this.unitOfWork;
  }

  String insertSQL(int numOfInsertions, DataMap dataMap) {
    return this.insertSQL(numOfInsertions, dataMap.getTableName(), dataMap.getAllColumnNames());
  }

  String insertSQL(int numOfInsertions, String tableName, List<String> columnNames) {
    String columns = String.join(", ", columnNames);
    List<String> placeholderList = new ArrayList<>();
    for (int i = 0; i < columnNames.size(); i++) {
      placeholderList.add("?");
    }
    String placeholders = String.join(", ", placeholderList);

    StringBuilder sb = new StringBuilder();
    sb.append("INSERT INTO ")
        .append(tableName)
        .append("(")
        .append(String.join(",", columns))
        .append(" VALUES ");
    for (int i = 0; i < numOfInsertions; i++) {
      sb.append("(").append(placeholders).append(")");
      if (i != numOfInsertions - 1) sb.append(", ");
    }
    String sql = sb.toString();
    return sql;
  }

  String deleteSQL(int numOfDeletions, DataMap dataMap, String matchCriteria) {
    return this.deleteSQL(numOfDeletions, dataMap.getTableName(), matchCriteria);
  }

  String deleteSQL(int numOfDeletions, String tableName, String matchCriteria) {
    StringBuilder sb = new StringBuilder();
    sb.append("DELETE FROM ").append(tableName).append(" WHERE ");
    for (int i = 0; i < numOfDeletions; i++) {
      sb.append("(").append(matchCriteria).append(")");
      if (i != numOfDeletions - 1) sb.append(" OR ");
    }
    String sql = sb.toString();
    return sql;
  }
}
