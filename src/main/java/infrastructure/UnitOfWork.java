package infrastructure;

/** Defines the contract for any class or interface that wishes to represent a unit of work. */
public interface UnitOfWork extends AutoCloseable {

  /*
   * Persists the unit of work.
   */
  void save();

  /** Discards the unit of work. */
  void undo();
}
