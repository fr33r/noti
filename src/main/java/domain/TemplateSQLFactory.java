package domain;

import infrastructure.DataMap;
import infrastructure.TemplateMetadata;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import org.jvnet.hk2.annotations.Service;

@Service
@Named("TemplateSQLFactory")
public class TemplateSQLFactory extends EntitySQLFactory<Template, UUID> {

  private final TemplateMetadata templateMetadata;
  private final Tracer tracer;

  @Inject
  public TemplateSQLFactory(Tracer tracer) {
    this.templateMetadata = new TemplateMetadata();
    this.tracer = tracer;
  }

  public Template reconstitute(Statement statement) {
    Span span =
        this.tracer
            .buildSpan("TemplateSQLFactory#reconstitute")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      if (statement == null) {
        return null;
      }
      Template template = null;
      if (statement.isClosed()) {
        return null;
      }
      template = this.extractTemplate(statement.getResultSet());
      return template;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }

  private Template extractTemplate(ResultSet results) throws SQLException {
    DataMap templateDataMap = this.templateMetadata.getDataMap();
    String uuidColumn = templateDataMap.getColumnNameForField(TemplateMetadata.UUID);
    String contentColumn = templateDataMap.getColumnNameForField(TemplateMetadata.CONTENT);

    String uuid = results.getString(uuidColumn);
    String content = results.getString(contentColumn);
    return new Template(UUID.fromString(uuid), content);
  }

  @Override
  public Template reconstitute(ResultSet... results) {
    Span span =
        this.tracer
            .buildSpan("TemplateSQLFactory#reconstitute")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      if (results == null || results.length < 1) {
        return null;
      }
      Template template = null;
      template = this.extractTemplate(results[0]);
      return template;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }
}
