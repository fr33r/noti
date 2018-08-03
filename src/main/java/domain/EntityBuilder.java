package domain;

// going with a builder because the construction of these objects can required a large number of
// parameters.
public abstract class EntityBuilder<T extends Entity> {

  public EntityBuilder() {}

  public abstract T build();
}
