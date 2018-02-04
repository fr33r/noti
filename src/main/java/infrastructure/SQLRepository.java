package infrastructure;

import infrastructure.SQLUnitOfWork;

/**
 *	An abstract class representing a repository that interacts with a database.
 */
public abstract class SQLRepository {

	private SQLUnitOfWork unitOfWork;

	/**
	 *	Constructs a {@link SQLRepository} instance, provided an instance of a class
	 *	that implements the {@link SQLUnitOfWork} interface.
	 *
	 *	@param unitOfWork	The instance of {@link SQLUnitOfWork} needed to create a new instance
	 *						of any class inheriting from this class.
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
