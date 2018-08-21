package infrastructure;

/**
 * Terminal expression representing a floating point number.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public final class FloatExpression extends QueryExpression {

  private final Float floatingPoint;

  /**
   * Constructs a new {@link FloatExpression}.
   *
   * @param floatingPoint The floating point number this expression represents.
   */
  public FloatExpression(float floatingPoint) {
    this(new Float(floatingPoint));
  }

  /**
   * Constructs a new {@link FloatExpression}.
   *
   * @param floatingPoint The floating point number this expression represents.
   */
  public FloatExpression(Float floatingPoint) {
    super();
    this.floatingPoint = floatingPoint;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public String interpret() {
    if (this.usePlaceholders()) return new PlaceholderExpression().interpret();
    return this.floatingPoint.toString();
  }
}
