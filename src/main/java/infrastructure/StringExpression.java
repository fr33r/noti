package infrastructure;

/**
 * Terminal expression representing a string.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public final class StringExpression extends QueryExpression {

  private final String string;

  /**
   * Constructs a new {@link StringExpression}.
   *
   * @param string The string that this expression represents.
   */
  public StringExpression(String string) {
    super();
    this.string = string;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public String interpret() {
    if (this.usePlaceholders()) return new PlaceholderExpression().interpret();
    return new StringBuilder().append("\"").append(this.string).append("\"").toString();
  }
}
