package net.shrimpworks.zomb.entities;

import java.io.IOException;
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
	 * @throws IOException something went wrong
	 */
	public boolean save(T entity) throws IOException;

	/**
	 * Delete a persisted entity.
	 *
	 * @param entity entity to be deleted
	 * @return true on success
	 * @throws IOException something went wrong
	 */
	public boolean delete(T entity) throws IOException;

	/**
	 * Load all persisted entities
	 *
	 * @return all entities
	 * @throws IOException something went wrong
	 */
	public Collection<T> all() throws IOException;
}
