package infrastructure;

/**
 * Terminal expression representing a boolean literal.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public final class BooleanLiteralExpression extends QueryExpression {

  private final Boolean bool;

  /**
   * Constructs a new {@link BooleanLiteralExpression}.
   *
   * @param bool The boolean literal that this expression represents.
   */
  public BooleanLiteralExpression(boolean bool) {
    this(new Boolean(bool));
  }

  /**
   * Constructs a new {@link BooleanLiteralExpression}.
   *
   * @param bool The boolean literal that this expression represents.
   */
  public BooleanLiteralExpression(Boolean bool) {
    this.bool = bool;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public String interpret() {
    if (this.usePlaceholders()) return new PlaceholderExpression().interpret();
    return this.bool.toString();
  }
}
