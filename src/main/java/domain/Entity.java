package domain;

/** @author jonfreer */
public abstract class Entity<I> {

  private I id;

  /**
   * Constructor for creating a transient entity. In other words, this constructor should be used to
   * construct an entity that has not yet been assigned an identity.
   */
  public Entity() {
    this.id = null;
  }

  /**
   * Constructor for creating an entity.
   *
   * @param id - The representation of this entities identity.
   */
  public Entity(I id) {
    this.id = id;
  }

  /**
   * Retrieves the identity for this entity.
   *
   * @return The identity of the calling {@link Entity} instance.
   */
  public I getId() {
    return this.id;
  }

  /**
   * Alters the identity of the entity to be the identity provided.
   *
   * @param id - The desired identity for the entity.
   */
  public void setId(I id) {
    this.id = id;
  }

  /** Compares the calling Entity instance to the provided object for equality. */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || this.getClass() != obj.getClass()) return false;

    return this.equals((Entity<I>) obj);
  }

  /**
   * Performs a strongly typed equality comparison between the calling Entity instance and the
   * provided Entity instance.
   *
   * @param entity - The Entity instance to compare to the calling instance.
   * @return true if: The two instance do not have an identity assigned and have the same memory
   *     address. The two instances do have an identity assigned and those identities are equal.
   *     false otherwise.
   */
  public boolean equals(Entity<I> entity) {
    if (entity == null) return false;

    if (this.id == null && entity.id == null && this == entity) {
      return true;
    }

    if (this.id != null && entity.id != null && this.id.equals(entity.id)) {
      return true;
    }

    return false;
  }

  /** Computes the integer hash code for the calling instance. */
  @Override
  public int hashCode() {
    final int prime = 17;
    int hashCode = 1;

    if (this.id != null) {
      hashCode = hashCode * prime + this.id.hashCode();
    }
    return hashCode * prime;
  }

  /**
   * Indicates if the entity is an aggregate root.
   *
   * @return {@code true} if the {@link Entity} is an aggregate root; {@code false} otherwise.
   */
  public abstract boolean isAggregateRoot();
}
