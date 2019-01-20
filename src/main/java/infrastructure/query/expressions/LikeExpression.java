package infrastructure.query.expressions;

/**
 * Nonterminal expression representing the pattern matching operation.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public final class LikeExpression extends QueryExpression {

  private final ColumnExpression columnExpression;
  private final StringExpression patternExpression;

  /**
   * Constructs a new {@link LikeExpression}.
   *
   * @param columnExpression The column expression of the pattern matching operation.
   * @param stringExpression The pattern within the pattern matching operation.
   */
  public LikeExpression(ColumnExpression columnExpression, StringExpression patternExpression) {
    super();
    this.columnExpression = columnExpression;
    this.patternExpression = patternExpression;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public String interpret() {
    return new StringBuilder()
        .append(this.columnExpression.interpret())
        .append(" LIKE ")
        .append(this.patternExpression.interpret())
        .toString();
  }
}
