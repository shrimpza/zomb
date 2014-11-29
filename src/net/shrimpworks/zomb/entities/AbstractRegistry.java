package net.shrimpworks.zomb.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractRegistry<T extends HasName> implements Registry<T> {

	private final Set<T> entities;

	public AbstractRegistry() {
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
	public List<T> all() {
		return Collections.unmodifiableList(new ArrayList<>(entities));
	}
}
