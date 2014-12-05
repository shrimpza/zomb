package net.shrimpworks.zomb.entities;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class RegistryImpl<T extends HasName> implements Registry<T> {

	private final Collection<T> entities;

	public RegistryImpl() {
		this.entities = new HashSet<>();
	}

	@Override
	public boolean add(T entity) {
		return entities.add(entity);
	}

	@Override
	public T remove(T entity) {
		return entities.remove(entity) ? entity : null;
	}

	@Override
	public T find(String name) {
		for (T entity : entities) {
			if (entity.name().equals(name)) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public Collection<T> all() {
		return Collections.unmodifiableCollection(entities);
	}
}
