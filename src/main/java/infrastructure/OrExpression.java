package infrastructure;

/**
 * Nonterminal expression representing a logical {@code OR} operation.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public final class OrExpression extends QueryExpression {

  private final QueryExpression left;
  private final QueryExpression right;

  /**
   * Constructs a new {@link OrExpression}.
   *
   * @param left The left operand of the logical {@code OR} operation.
   * @param right The right operand of the logical {@code OR} operation.
   */
  public OrExpression(QueryExpression left, QueryExpression right) {
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
        .append(" OR ")
        .append(this.right.interpret())
        .toString();
  }
}
