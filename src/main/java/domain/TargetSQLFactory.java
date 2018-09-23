package domain;

import infrastructure.DataMap;
import infrastructure.TargetMetadata;
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
@Named("TargetSQLFactory")
public class TargetSQLFactory extends EntitySQLFactory<Target, UUID> {

  private final TargetMetadata targetMetadata;
  private final Tracer tracer;

  @Inject
  public TargetSQLFactory(Tracer tracer) {
    this.targetMetadata = new TargetMetadata();
    this.tracer = tracer;
  }

  public Target reconstitute(Statement statement) {
    Span span =
        this.tracer
            .buildSpan("TargetSQLFactory#reconstitute")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      if (statement == null) {
        return null;
      }
      Target target = null;
      if (statement.isClosed()) {
        return null;
      }
      target = this.extractTarget(statement.getResultSet());
      return target;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }

  private Target extractTarget(ResultSet results) throws SQLException {
    DataMap targetDataMap = this.targetMetadata.getDataMap();
    String uuidColumn = targetDataMap.getColumnNameForField(TargetMetadata.UUID);
    String nameColumn = targetDataMap.getColumnNameForField(TargetMetadata.NAME);
    String phoneNumberColumn = targetDataMap.getColumnNameForField(TargetMetadata.PHONE_NUMBER);

    String uuid = results.getString(uuidColumn);
    String name = results.getString(nameColumn);
    String phoneNumber = results.getString(phoneNumberColumn);
    return new Target(UUID.fromString(uuid), name, new PhoneNumber(phoneNumber));
  }

  @Override
  public Target reconstitute(ResultSet... results) {
    Span span =
        this.tracer
            .buildSpan("TargetSQLFactory#reconstitute")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      if (results == null || results.length < 1) {
        return null;
      }
      Target target = null;
      target = this.extractTarget(results[0]);
      return target;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }
}
