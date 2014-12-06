package net.shrimpworks.zomb.entities;

import java.io.IOException;

/**
 * A generic registry implementation which passes added or removed entities to
 * persistence for storage.
 *
 * On creation, it will restore it's contents from storage.
 *
 * @param <T> entity type
 */
public class PersistentRegistry<T extends HasName> extends RegistryImpl<T> implements Registry<T> {

	private final Persistence<T> persistence;

	public PersistentRegistry(Persistence<T> persistence) throws IOException {
		this.persistence = persistence;

		persistence.all().forEach(this::add);
	}

	@Override
	public boolean add(T entity) {
		if (super.add(entity)) {
			try {
				persistence.save(entity);

				return true;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return false;
	}

	@Override
	public T remove(T entity) {
		T res;
		if ((res = super.remove(entity)) != null) {
			try {
				persistence.delete(res);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return res;
	}
}
