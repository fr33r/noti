package infrastructure;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.inject.Named;
import org.jvnet.hk2.annotations.Service;

/** Factory that creates {@link MySQLUnitOfWork} instances. */
@Service
public class MySQLUnitOfWorkFactory implements SQLUnitOfWorkFactory {

  private String jdbcURL;
  private String username;
  private String password;
  private final Tracer tracer;

  /**
   * Constructs a new {@link MySQLUnitOfWorkFactory}.
   *
   * @param jdbcURL The JDBC URL required to create a connection to MySQL.
   * @param username The username required to create a connection to MySQL.
   * @param password The password required to create a connection to MySQL.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   */
  @Inject
  public MySQLUnitOfWorkFactory(
      @Named("JDBC_URL") final String jdbcURL,
      @Named("JDBC_USERNAME") final String username,
      @Named("JDBC_PASSWORD") final String password,
      Tracer tracer) {
    this.jdbcURL = jdbcURL;
    this.username = username;
    this.password = password;
    this.tracer = tracer;
  }

  /**
   * Creates a new {@link MySQLUnitOfWork}.
   *
   * @return The new {@link MySQLUnitOfWork}.
   */
  public SQLUnitOfWork create() {
    Span span =
        this.tracer
            .buildSpan("MySQLUnitOfWorkFactory#create")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Connection connection =
          DriverManager.getConnection(this.jdbcURL, this.username, this.password);
      connection.setAutoCommit(false);
      return new MySQLUnitOfWork(connection, this.tracer);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } finally {
      span.finish();
    }
  }
}
