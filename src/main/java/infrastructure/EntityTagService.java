package infrastructure;

import javax.ws.rs.core.EntityTag;

import org.jvnet.hk2.annotations.Contract;

/**
 * Defines the contract that must be fulfilled for all implementing classes
 * that have the intention of generating HTTP Entity Tags (ETags).
 * @author jonfreer
 * @since 12/30/17
 */
@Contract
public interface EntityTagService {
	
	/**
	 * Calculates an entity tag based on the provided representation.
	 * @param entity The representation that the generated entity tag is based off of.
	 * @return An instance of {@link EntityTag}, representing the entity tag generated.
	 */
	<T> EntityTag generateTag(T entity);
}
