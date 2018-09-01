package infrastructure;

/**
 * Terminal expression representing the descending sort order.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public class DescendingExpression extends QueryExpression {

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
