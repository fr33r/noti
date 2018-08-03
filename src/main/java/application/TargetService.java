package application;

import java.util.UUID;

/**
 * Defines the abstraction that exposes various application operations for targets.
 *
 * @author Jon Freer
 */
public interface TargetService {

  /**
   * Creates a new {@link application.Target}.
   *
   * @param target The state of the {@link application.Target} to create.
   * @return The universally unique identifer of the newly created {@link application.Target}.
   */
  UUID createTarget(Target target);

  /**
   * Retrieves an existing {@link application.Target}.
   *
   * @param uuid The universally unique identifer of the {@link application.Target} being retrieved.
   * @return The {@link application.Target} with the universally unique identifer provided.
   */
  Target getTarget(UUID uuid);

  /**
   * Replaces the current state of the {@link application.Target} with the state provided.
   *
   * @param target The desired state of the {@link application.Target}.
   */
  void replaceTarget(Target target);

  /**
   * Deletes an existing {@link application.Target}.
   *
   * @param uuid The universally unique identifier of the {@link application.Target} to delete.
   */
  void deleteTarget(UUID uuid);
}
