package infrastructure;

/**
 * Nonterminal expression representing the pattern matching operation.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public final class LikeExpression extends QueryExpression {

  private final QueryExpression left;
  private final QueryExpression right;

  /**
   * Constructs a new {@link LikeExpression}.
   *
   * @param left The left operand of the pattern matching operation.
   * @param right The right operand of the pattern matching operation.
   */
  public LikeExpression(QueryExpression left, QueryExpression right) {
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
        .append(" LIKE ")
        .append(this.right.interpret())
        .toString();
  }
}
