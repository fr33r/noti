package infrastructure;

/**
 * Defines the abstraction representing a collection of entities.
 *
 * @author Jon Freer
 */
public abstract class SQLRepository {

  private SQLUnitOfWork unitOfWork;

  /**
   * Constructs a new {@link SQLRepository}.
   *
   * @param unitOfWork The unit of work that this repository will contribue to.
   */
  public SQLRepository(SQLUnitOfWork unitOfWork) {
    this.unitOfWork = unitOfWork;
  }

  /**
   * Retrieves the unit of work for the repository.
   *
   * @return The unit of work for the repository.
   */
  public SQLUnitOfWork getUnitOfWork() {
    return this.unitOfWork;
  }
}
