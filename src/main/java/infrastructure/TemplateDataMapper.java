package infrastructure;

import domain.EntitySQLFactory;
import domain.Template;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;

public final class TemplateDataMapper extends DataMapper<Template> {

  private final EntitySQLFactory<Template, UUID> templateFactory;
  private final TemplateMetadata templateMetadata;
  private final Logger logger;

  TemplateDataMapper(
      Connection connection, EntitySQLFactory<Template, UUID> templateFactory, Logger logger) {
    super(connection);

    this.templateFactory = templateFactory;
    this.templateMetadata = new TemplateMetadata();
    this.logger = logger;
  }

  private String findTemplateSQL() {
    DataMap templateDataMap = this.templateMetadata.getDataMap();
    List<String> columnNames = templateDataMap.getAllColumnNames();
    String columns = String.join(", ", columnNames);

    StringBuilder sb =
        new StringBuilder()
            .append("SELECT ")
            .append(columns)
            .append(" FROM ")
            .append(templateDataMap.getTableName())
            .append(" AS ")
            .append(templateDataMap.getTableAlias())
            .append(" WHERE ")
            .append(templateDataMap.getTableAlias())
            .append(".")
            .append(templateDataMap.getColumnNameForField(TemplateMetadata.UUID))
            .append(" = ?");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String insertTemplateSQL() {
    DataMap templateDataMap = this.templateMetadata.getDataMap();
    String sql = this.insertSQL(1, templateDataMap);
    this.logger.debug(sql);
    return sql;
  }

  private String updateTemplateSQL() {
    DataMap templateDataMap = this.templateMetadata.getDataMap();

    StringBuilder sb =
        new StringBuilder()
            .append("UPDATE ")
            .append(templateDataMap.getTableName())
            .append(" SET ")
            .append(templateDataMap.getColumnNameForField(TemplateMetadata.CONTENT))
            .append(" = ?")
            .append(" WHERE ")
            .append(templateDataMap.getColumnNameForField(TemplateMetadata.UUID))
            .append(" = ?");

    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  private String deleteTemplateSQL() {
    DataMap templateDataMap = this.templateMetadata.getDataMap();
    String matchCriteria =
        new StringBuilder()
            .append(templateDataMap.getColumnNameForField(TemplateMetadata.UUID))
            .append(" = ?")
            .toString();

    String sql = this.deleteSQL(1, templateDataMap, matchCriteria);
    this.logger.debug(sql);
    return sql;
  }

  private String countTemplatesSQL() {
    DataMap templateDataMap = this.templateMetadata.getDataMap();
    StringBuilder sb =
        new StringBuilder()
            .append("SELECT ")
            .append("COUNT(*) ")
            .append("FROM ")
            .append(templateDataMap.getTableName())
            .append(" AS ")
            .append(templateDataMap.getTableAlias());
    String sql = sb.toString();
    this.logger.debug(sql);
    return sql;
  }

  @Override
  public Template find(final UUID uuid) {

    final String templateSQL = this.findTemplateSQL();

    Template template = null;
    try (PreparedStatement getTemplateStatement =
        this.getConnection().prepareStatement(templateSQL)) {

      int index = 1;
      getTemplateStatement.setString(index, uuid.toString());
      try (ResultSet templateRs = getTemplateStatement.executeQuery()) {
        if (templateRs.next()) {
          template = this.templateFactory.reconstitute(templateRs);
        }
      }
      return template;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  @Override
  public void insert(final Template template) {

    final String insertTemplateSQL = this.insertTemplateSQL();

    try (PreparedStatement insertTemplateStatement =
        this.getConnection().prepareStatement(insertTemplateSQL)) {

      int index = 0;
      insertTemplateStatement.setString(++index, template.getId().toString());
      insertTemplateStatement.setString(++index, template.getContent());
      insertTemplateStatement.executeUpdate();
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  @Override
  public void update(final Template template) {

    final String updateTemplateSQL = this.updateTemplateSQL();

    try (PreparedStatement updateTemplateStatement =
        this.getConnection().prepareStatement(updateTemplateSQL)) {

      int index = 0;
      updateTemplateStatement.setString(++index, template.getContent());
      updateTemplateStatement.setString(++index, template.getId().toString());
      updateTemplateStatement.executeUpdate();
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  @Override
  public void delete(final UUID uuid) {

    final String deleteTemplateSQL = this.deleteTemplateSQL();
    try (PreparedStatement deleteTemplateStatement =
        this.getConnection().prepareStatement(deleteTemplateSQL)) {

      int index = 1;
      deleteTemplateStatement.setString(index, uuid.toString());
      deleteTemplateStatement.executeUpdate();
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  @Override
  public int count() {

    final String countTemplatesSQL = this.countTemplatesSQL();
    try (final PreparedStatement countTemplatesStatement =
            this.getConnection().prepareStatement(countTemplatesSQL);
        final ResultSet rs = countTemplatesStatement.executeQuery()) {
      int index = 1;
      rs.next();
      return rs.getInt(index);
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }
}
