package infrastructure;

/**
 * Nonterminal expression representing a logical {@code AND} operation.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public final class AndExpression extends QueryExpression {

  private final QueryExpression left;
  private final QueryExpression right;

  /**
   * Constructs a new {@link AndExpression}.
   *
   * @param left The left operand of the logical {@code AND} operation.
   * @param right The right operand of the logical {@code AND} operation.
   */
  public AndExpression(QueryExpression left, QueryExpression right) {
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
        .append(" AND ")
        .append(this.right.interpret())
        .toString();
  }
}
