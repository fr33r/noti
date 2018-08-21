package infrastructure;

/**
 * Nonterminal expression representing the less-than-or-equal-to operation.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public final class LessThanOrEqualToExpression extends QueryExpression {

  private final QueryExpression left;
  private final QueryExpression right;

  /**
   * Constructs a new {@link LessThanOrEqualToExpression}.
   *
   * @param left The left operand of the less-than-or-equal-to operation.
   * @param right The right operand of the less-than-or-equal-to operation.
   */
  public LessThanOrEqualToExpression(QueryExpression left, QueryExpression right) {
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
        .append(" <= ")
        .append(this.right.interpret())
        .toString();
  }
}
