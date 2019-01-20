package infrastructure.query.expressions;

/**
 * Nonterminal expression representing the order by expression.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public class OrderByExpression extends QueryExpression {

  private final ColumnListExpression columnListExpression;
  private final TerminalExpression sortExpression;

  /**
   * Constructs a new {@link OrderByExpression}.
   *
   * @param columnListExpression The column list of the sort expression.
   * @param sortExpression The sort expression indicating the sort direction.
   */
  public OrderByExpression(
      ColumnListExpression columnListExpression, TerminalExpression sortExpression) {
    super();
    this.columnListExpression = columnListExpression;
    this.sortExpression = sortExpression;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public String interpret() {
    return new StringBuilder()
        .append(this.columnListExpression.interpret())
        .append(" ")
        .append(this.sortExpression.interpret())
        .toString();
  }
}
