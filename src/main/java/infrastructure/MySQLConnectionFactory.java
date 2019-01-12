package infrastructure;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.inject.Named;

public class MySQLConnectionFactory extends ConnectionFactory {

  private final String jdbcURL;
  private final String username;
  private final String password;
  private final Tracer tracer;

  /**
   * Constructs a new {@link MySQLConnectionFactory}.
   *
   * @param jdbcURL The JDBC URL required to create a connection to MySQL.
   * @param username The username required to create a connection to MySQL.
   * @param password The password required to create a connection to MySQL.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   */
  @Inject
  public MySQLConnectionFactory(
      @Named("JDBC_URL") final String jdbcURL,
      @Named("JDBC_USERNAME") final String username,
      @Named("JDBC_PASSWORD") final String password,
      Tracer tracer) {
    this.jdbcURL = jdbcURL;
    this.username = username;
    this.password = password;
    this.tracer = tracer;
  }

  @Override
  public Connection createConnection() {
    String className = MySQLConnectionFactory.class.getName();
    String spanName = String.format("%s#createConnection", className);
    Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Connection connection =
          DriverManager.getConnection(this.jdbcURL, this.username, this.password);
      connection.setAutoCommit(false);
      return connection;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      span.finish();
    }
  }
}
