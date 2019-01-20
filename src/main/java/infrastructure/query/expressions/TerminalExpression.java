package infrastructure.query.expressions;

public abstract class TerminalExpression extends QueryExpression {

  private boolean usePlaceholders;

  /** Constructs a new {@link TerminalExpression}. */
  public TerminalExpression() {
    this(false);
  }

  /**
   * Constructs a new {@link TerminalExpression}.
   *
   * @param usePlaceholders Indicates whether to utilize placeholders within the expression.
   */
  public TerminalExpression(boolean usePlaceholders) {
    super();
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
}
