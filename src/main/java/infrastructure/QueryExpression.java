package infrastructure;

/**
 * Represents an expression with a query.
 *
 * <p>Implementation is based off of the Interpreter pattern.
 *
 * @author Jon Freer
 */
public abstract class QueryExpression {

  /**
   * Evaluates the expression.
   *
   * @return The textual representation of the expression.
   */
  public abstract String interpret();
}
