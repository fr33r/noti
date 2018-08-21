package infrastructure;

/**
 * Nonterminal expression representing the not-equal-to operation.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public final class NotEqualToExpression extends QueryExpression {

  private final QueryExpression left;
  private final QueryExpression right;

  /**
   * Constructs a new {@link NotEqualToExpression}.
   *
   * @param left The left operand of the not-equal-to operation.
   * @param right The right operand of the not-equal-to operation.
   */
  public NotEqualToExpression(QueryExpression left, QueryExpression right) {
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
        .append(" <> ")
        .append(this.right.interpret())
        .toString();
  }
}
