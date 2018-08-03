package infrastructure;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Represents a unit of work in the context of MySQL database interactions. */
public class MySQLUnitOfWork implements SQLUnitOfWork {

  private Connection connection;
  private boolean hasActed;
  private final Tracer tracer;

  /**
   * Constructs a new instance provided an instance of {@link Connection}. It is in the context of
   * this connection that the unit of work will be utilized. It is recommended that an instance of
   * this class is created by using the DatabaseUnitOfWorkFactory class as opposed to invoking this
   * constructor directly.
   *
   * @param connection The connection that this DatabaseUnitOfWork instance will be used with.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   */
  protected MySQLUnitOfWork(Connection connection, Tracer tracer) {
    this.connection = connection;
    this.hasActed = false;
    this.tracer = tracer;
  }

  /** Saves (commits) the unit of work to the MySQL database. */
  @Override
  public void save() {
    final Span span =
        this.tracer.buildSpan("MySQLUnitOfWork#save").asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      if (hasActed) {
        throw new IllegalStateException("Cannot call save() or undo() multiple times.");
      }

      this.connection.commit();
      this.hasActed = true;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        this.connection.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
      span.finish();
    }
  }

  /** Undoes (performs a rollback for) the unit of work. */
  @Override
  public void undo() {
    final Span span =
        this.tracer.buildSpan("MySQLUnitOfWork#undo").asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      if (hasActed) {
        throw new IllegalStateException("Cannot call save() or undo() multiple times.");
      }
      this.connection.rollback();
      this.hasActed = true;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        this.connection.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
      span.finish();
    }
  }

  /**
   * Constructs an instance of PreparedStatement in the context of this unit of work. Use this
   * method to add changes to the unit of work.
   *
   * @param sql The parameterized SQL to execute during this unit of work.
   * @return An instance of PreparedStatement representing a single parameterized SQL statement to
   *     execute for this unit of work.
   */
  @Override
  public PreparedStatement createPreparedStatement(String sql) {
    final Span span =
        this.tracer
            .buildSpan("MySQLUnitOfWork#createPreparedStatement")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return this.connection.prepareStatement(sql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      span.finish();
    }
  }

  /**
   * Constructs an instance of CallableStatement in the context of this unit of work. Use this
   * method to add changes to the unit of work when working with stored procedures.
   *
   * @param sql The JDBC escaped syntax SQL statement.
   * @return An instance of CallableStatement representing a single JDBC escape syntax SQL statement
   *     to execute for this unit of work.
   */
  @Override
  public CallableStatement createCallableStatement(String sql) {
    final Span span =
        this.tracer
            .buildSpan("MySQLUnitOfWork#createCallableStatement")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return this.connection.prepareCall(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } finally {
      span.finish();
    }
  }

  /**
   * Closes the provided statement to release resources. All statements that have been created for
   * this unit of work should be immediately closed once they are no longer in use.
   *
   * @param statement The statement to be destroyed.
   */
  @Override
  public void destroyStatement(PreparedStatement statement) {
    this.destroyStatements(statement);
  }

  /**
   * Closes the provided statements to release resources. All statements that have been created for
   * this unit of work should be immediately closed once they are no longer in use.
   *
   * @param statements The statements to be destroyed.
   */
  @Override
  public void destroyStatements(PreparedStatement... statements) {
    try {
      for (PreparedStatement statement : statements) {
        if (statement != null && !statement.isClosed()) {
          statement.close();
        }
      }
    } catch (SQLException sqlEx) {
      sqlEx.printStackTrace();
      throw new RuntimeException(sqlEx);
    }
  }

  @Override
  public void close() throws Exception {
    if (!this.hasActed) {
      this.undo();
    }
  }
}
