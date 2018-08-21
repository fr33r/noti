package infrastructure;

/**
 * Nonterminal expression representing the equality operation.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public final class EqualToExpression extends QueryExpression {

  private final QueryExpression left;
  private final QueryExpression right;

  /**
   * Constructs a new {@link EqualToExpression}.
   *
   * @param left The left operand of the equality operation.
   * @param right the right operation of the equality operation.
   */
  public EqualToExpression(QueryExpression left, QueryExpression right) {
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
        .append(" = ")
        .append(this.right.interpret())
        .toString();
  }
}
