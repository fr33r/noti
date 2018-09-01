package infrastructure;

/**
 * Represents an expression with a query.
 *
 * <p>Implementation is based off of the Interpreter pattern.
 *
 * @author Jon Freer
 */
public abstract class QueryExpression {

  // TODO - perhaps i should create a TerminalQueryExpression
  // class that defines the placeholder code - it only seems
  // relevant for terminal expressions.
  private boolean usePlaceholders;

  /** Constructs a new {@link QueryExpression}. */
  public QueryExpression() {
    this(false);
  }

  /**
   * Constructs a new {@link QueryExpression}.
   *
   * @param usePlaceholders Indicates whether to utilize placeholders within the expression.
   */
  public QueryExpression(boolean usePlaceholders) {
    this.usePlaceholders(usePlaceholders);
  }

  /**
   * Specifies whether to utilize a placeholder within the expression.
   *
   * @param usePlaceholders Indicates whether to utilize placeholders within the expression.
   */
  public void usePlaceholders(boolean usePlaceholders) {
    this.usePlaceholders = usePlaceholders;
  }

  /**
   * Indicates whether placeholders are utilized within the expression.
   *
   * @return {@code true} if placeholders are used; {@code false} otherwise.
   */
  public boolean usePlaceholders() {
    return this.usePlaceholders;
  }

  /**
   * Evaluates the expression.
   *
   * @return The textual representation of the expression.
   */
  public abstract String interpret();
}
