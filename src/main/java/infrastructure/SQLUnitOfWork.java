package infrastructure;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;

/**
 * Defines the contract for any class or interface that wishes to represent a unit of work in the
 * context of an SQL database.
 */
public interface SQLUnitOfWork extends UnitOfWork {

  /**
   * Constructs a prepared statement given an SQL string.
   *
   * @param sql The parameterized SQL statement.
   * @return A prepared statement that can be used to contribute to the unit of work.
   */
  PreparedStatement createPreparedStatement(String sql);

  /**
   * Constructs a callable statement given an SQL string.
   *
   * @param sql The JDBC escaped syntax SQL statement.
   * @return A callable statement that can be used to contribute to the unit of work.
   */
  CallableStatement createCallableStatement(String sql);

  /**
   * Closes the provided statement to release resources. All statements that have been created for
   * this unit of work should be immediately closed once they are no longer in use.
   *
   * @param statement The statement to be destroyed.
   */
  void destroyStatement(PreparedStatement statement);

  /**
   * Closes the provided statements to release resources. All statements that have been created for
   * this unit of work should be immediately closed once they are no longer in use.
   *
   * @param statements The statements to be destroyed.
   */
  void destroyStatements(PreparedStatement... statements);
}
