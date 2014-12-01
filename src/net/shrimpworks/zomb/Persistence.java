package net.shrimpworks.zomb;

import java.util.Collection;

/**
 * Provides a simple interface for saving and restoring entities.
 *
 * @param <T> entity type to save, delete and load
 */
public interface Persistence<T> {

	/**
	 * Persist an entity.
	 *
	 * @param entity entity to be persisted
	 * @return true on success
	 */
	public boolean save(T entity);

	/**
	 * Delete a persisted entity.
	 *
	 * @param entity entity to be deleted
	 * @return true on success
	 */
	public boolean delete(T entity);

	/**
	 * Load all persisted entities
	 *
	 * @return all entities
	 */
	public Collection<T> all();
}
