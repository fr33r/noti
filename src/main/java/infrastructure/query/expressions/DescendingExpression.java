package infrastructure.query.expressions;

/**
 * Terminal expression representing the descending sort order.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public class DescendingExpression extends TerminalExpression {

  /** Constructs a new {@link DescendingExpression}. */
  public DescendingExpression() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public String interpret() {
    return "DESC";
  }
}
