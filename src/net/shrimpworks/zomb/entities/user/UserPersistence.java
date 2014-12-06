package net.shrimpworks.zomb.entities.user;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.eclipsesource.json.JsonObject;
import net.shrimpworks.zomb.entities.Persistence;

public class UserPersistence implements Persistence<User> {

	private final Persistence<JsonObject> persistence;

	public UserPersistence(Persistence<JsonObject> persistence) {
		this.persistence = persistence;
	}

	@Override
	public boolean save(User entity) throws IOException {
		JsonObject json = new JsonObject().add("name", entity.name());
		return persistence.save(json);
	}

	@Override
	public boolean delete(User entity) throws IOException {
		return persistence.delete(new JsonObject().add("name", entity.name()));
	}

	@Override
	public Collection<User> all() throws IOException {
		Collection<JsonObject> json = persistence.all();

		Set<User> all = json.stream().map(j -> new UserImpl(
				j.get("name").asString()
		)).collect(Collectors.toSet());

		return Collections.unmodifiableCollection(all);
	}
}
