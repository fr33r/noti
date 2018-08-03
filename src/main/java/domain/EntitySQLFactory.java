package domain;

import java.sql.ResultSet;
import java.sql.Statement;
import org.jvnet.hk2.annotations.Contract;

/**
 * Responsible for entity reconstitution. In other words, this factory is responsible for
 * constructing an entity using its persisted form as input.
 *
 * @author jonfreer
 * @param <T> The entity that this factory reconstitutes.
 * @param <I> The identifier associated to the entity being reconstituted.
 */
@Contract
public abstract class EntitySQLFactory<T extends Entity<I>, I> {

  /**
   * Reconstitutes the entity using the provided {@link Statement} instance.
   *
   * @param statement The {@link Statement} instance used to construct the entity.
   * @return The reconstituted entity.
   */
  public abstract T reconstitute(Statement statement);

  /**
   * Reconstitutes the entity using the provided {@link ResultSet} instances.
   *
   * @param results The {@link ResultSet} instances used to construct the entity.
   * @return The reconstituted entity.
   */
  public abstract T reconstitute(ResultSet... results);
}
