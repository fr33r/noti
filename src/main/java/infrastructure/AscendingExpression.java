package infrastructure;

/**
 * Terminal expression representing the ascending sort order.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public class AscendingExpression extends QueryExpression {

  /** Constructs a new {@link AscendingExpression}. */
  public AscendingExpression() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public String interpret() {
    return "ASC";
  }
}
