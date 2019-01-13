package infrastructure;

/**
 * Terminal expression representing a placeholder.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public final class PlaceholderExpression extends TerminalQueryExpression {

  /** Constructs a new {@link PlaceholderExpression}. */
  public PlaceholderExpression() {
    super(true);
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public String interpret() {
    return "?";
  }
}
