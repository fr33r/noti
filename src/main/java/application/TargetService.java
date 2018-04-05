package application;

import application.Target;

import java.util.UUID;

/**
 * Defines the contract for all application services that wish to provide
 * application behavior related to targets.
 *
 * @author Jon Freer
 */
public interface TargetService {

	/**
	 * 
	 * @param target
	 * @return The universally unique identifer of the newly created target.
	 */
	UUID createTarget(Target target);

	/**
	 * 
	 * @param uuid The universally unique identifer of the target desired.
	 * @return The target with the universally unique identifer provided.
	 */
	Target getTarget(UUID uuid);

	/**
	 * 
	 * @param target The desired representation of the target. 
	 */
	void replaceTarget(Target target);

	/**
	 * 
	 * @param uuid The universally unique identifier for the target to delete.
	 */
	void deleteTarget(UUID uuid);
}
