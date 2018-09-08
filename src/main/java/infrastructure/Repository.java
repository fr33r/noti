package infrastructure;

import domain.Entity;
import java.util.Set;

/**
 * Responsible for retrieving and persisting an entities, while emulating a collection.
 *
 * @author jonfreer
 * @param T The type of the entity that this repository "contains".
 * @param I The type of the entity identifier of the entity that this repository "contains".
 */
public interface Repository<T extends Entity<I>, I> {

  /**
   * Retrieves the entity from the repository by a representation of the entity's identity.
   *
   * @param id The representation of the identity of the entity to be retrieved.
   * @return The entity with the identity provided.
   */
  T get(I id);

  /**
   * Places the entity provided into the repository. If the entity provided already exists in the
   * repository, it's state is replaced with the state provided.
   *
   * @param entity The entity to placed within the repository.
   */
  void put(T entity);

  /**
   * Inserts the entity provided into the repository.
   *
   * @param entity The entity to be placed within the repository for the first time.
   */
  void add(T entity);

  /**
   * Removes the entity from the repository with the representation of the identity provided.
   *
   * @param id The representation of the identity of the entity to be removed.
   */
  void remove(I id);

  /**
   * Retrieves the entities matching the provided {@link Query}.
   *
   * @param query The {@link Query} to match against.
   * @return The entities matching the provided {@link Query}.
   */
  Set<T> get(Query<T> query);

  /**
   * Retrieves the number of entities within the repository.
   *
   * @return The number of entities within the repository.
   */
  int size();
}
