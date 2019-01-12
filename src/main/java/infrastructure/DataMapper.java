package infrastructure;

import domain.Entity;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

abstract class DataMapper<T extends Entity> {

  private final Connection connection;

  DataMapper(Connection connection) {
    this.connection = connection;
  }

  Connection getConnection() {
    return this.connection;
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
        .append(")")
        .append(" VALUES ");
    for (int i = 0; i < numOfInsertions; i++) {
      sb.append("(").append(placeholders).append(")");
      if (i != numOfInsertions - 1) sb.append(", ");
    }
    String sql = sb.toString();
    return sql;
  }

  abstract void insert(T entity);

  abstract void update(T entity);

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

  abstract void delete(UUID uuid);

  abstract int count();

  abstract T find(UUID uuid);
}
