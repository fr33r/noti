package infrastructure;

/**
 * Nonterminal expression representing the sort expression.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public class OrderByExpression extends QueryExpression {

  private final QueryExpression left;
  private final QueryExpression right;

  /**
   * Constructs a new {@link OrderByExpression}.
   *
   * @param left The left operand of the sort expression.
   * @param right The right operand of the sort expression.
   */
  public OrderByExpression(QueryExpression left, QueryExpression right) {
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
        .append(" ")
        .append(this.right.interpret())
        .toString();
  }
}
