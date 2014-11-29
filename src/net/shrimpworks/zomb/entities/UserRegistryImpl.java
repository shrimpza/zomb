package net.shrimpworks.zomb.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserRegistryImpl implements UserRegistry {

	private final Set<User> users;

	public UserRegistryImpl() {
		this.users = new HashSet<>();
	}

	@Override
	public boolean add(User entity) {
		return users.add(entity);
	}

	@Override
	public User remove(User entity) {
		return users.remove(entity) ? entity : null;
	}

	@Override
	public User find(String name) {
		for (User user : users) {
			if (user.name().equals(name)) {
				return user;
			}
		}
		return null;
	}

	@Override
	public List<User> all() {
		return Collections.unmodifiableList(new ArrayList<>(users));
	}
}
