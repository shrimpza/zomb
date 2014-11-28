package net.shrimpworks.zomb.entities;

import java.util.List;

/**
 * The Registry is responsible for holding and managing all registered entities.
 *
 * @param <T> a named entity
 */
public interface Registry<T extends HasName> {

	/**
	 * Add an entity to this registry.
	 *
	 * @param entity entity to add
	 * @return true if the entity was added
	 */
	public boolean add(T entity);

	/**
	 * Remove an entity from this registry.
	 *
	 * @param entity entity to remove
	 * @return removed entity
	 */
	public T remove(T entity);

	/**
	 * Find an entity by name.
	 *
	 * @param name name to search for
	 * @return found entity, or null
	 */
	public T find(String name);

	/**
	 * Get all entities within this registry.
	 * <p>
	 * List type is used, since ordering may be important for certain
	 * implementations.
	 *
	 * @return all entities
	 */
	public List<T> all();
}
