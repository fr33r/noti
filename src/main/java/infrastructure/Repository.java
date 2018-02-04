package infrastructure;

import domain.Entity;

/**
 * Responsible for retrieving and persisting an entities, while emulating a collection.
 * @author jonfreer
 *
 * @param <T> The type of the entity that this repository "contains".
 * @param <I> The type of the entity identifier of the entity that this repository "contains".
 */
public interface Repository<T extends Entity<I>, I> {

	/**
	 *	Retrieves the entity from the repository by a representation of the entity's identity.
	 */
	T get(I id);

	/**
	 *	Places the entity provided into the repository. If the entity provided already exists
	 *	in the repository, it's state is replaced with the state provided.
	 */
	void put(T entity);

	/**
	 *	Inserts the entity provided into the repository. 
	 */
	void add(T entity);

	/**
	 *	Removes the entity from the repository with the representation of the identity provided.
	 */
	void remove(I id);
}
