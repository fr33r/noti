package domain;

public abstract class ValueObject {

  /**
   * Compares whether the calling ValueObject instance is equal to the provided instance.
   *
   * <p>Value objects are considered equal if the values of one match the values of the other. In
   * other words, two value objects can't be considered equal unless their values (class members)
   * match.
   */
  @Override
  public abstract boolean equals(Object obj);

  @Override
  public abstract int hashCode();
}
