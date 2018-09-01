package infrastructure;

/**
 * Terminal expression representing an integer.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public final class IntegerExpression extends QueryExpression {

  private final Integer integer;

  /**
   * Constructs a new {@link IntegerExpression}.
   *
   * @param integer The integer that this expression represents.
   */
  public IntegerExpression(int integer) {
    this(new Integer(integer));
  }

  /**
   * Constructs a new {@link IntegerExpression}.
   *
   * @param integer The integer that this expression represents.
   */
  public IntegerExpression(Integer integer) {
    super();
    this.integer = integer;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public String interpret() {
    if (this.usePlaceholders()) return new PlaceholderExpression().interpret();
    return this.integer.toString();
  }
}
