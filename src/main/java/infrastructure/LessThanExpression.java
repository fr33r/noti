package infrastructure;

/**
 * Nonterminal expression representing the less-than operation.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public final class LessThanExpression extends QueryExpression {

  private final QueryExpression left;
  private final QueryExpression right;

  /**
   * Constructs a new {@link LessThanExpression}.
   *
   * @param left The left operand of the less-than operation.
   * @param right The right operand of the right-than operation.
   */
  public LessThanExpression(QueryExpression left, QueryExpression right) {
    super();
    this.left = left;
    this.right = right;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public String interpret() {
    return new StringBuilder()
        .append(this.left.interpret())
        .append(" < ")
        .append(this.right.interpret())
        .toString();
  }
}
