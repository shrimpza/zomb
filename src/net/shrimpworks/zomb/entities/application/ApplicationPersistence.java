package net.shrimpworks.zomb.entities.application;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.eclipsesource.json.JsonObject;
import net.shrimpworks.zomb.entities.Persistence;
import net.shrimpworks.zomb.entities.PersistentRegistry;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.user.User;

public class ApplicationPersistence implements Persistence<Application> {

	private static final Logger logger = Logger.getLogger(ApplicationPersistence.class.getName());

	private final Persistence<JsonObject> persistence;
	private final Persistence<Plugin> pluginPersistence;
	private final Persistence<User> userPersistence;

	public ApplicationPersistence(Persistence<JsonObject> persistence, Persistence<Plugin> pluginPersistence, Persistence<User> userPersistence) {
		this.persistence = persistence;
		this.pluginPersistence = pluginPersistence;
		this.userPersistence = userPersistence;
	}

	@Override
	public boolean save(Application entity) throws IOException {
		JsonObject json = new JsonObject()
				.add("name", entity.name())
				.add("key", entity.key())
				.add("contact", entity.contact())
				.add("url", entity.url());
		return persistence.save(json);
	}

	@Override
	public boolean delete(Application entity) throws IOException {
		return persistence.delete(new JsonObject().add("name", entity.name()));
	}

	@Override
	public Collection<Application> all() throws IOException {
		Collection<JsonObject> json = persistence.all();

		Set<Application> all = json.stream().map(j -> {
			try {
				return new ApplicationImpl(
						j.get("name").asString(),
						j.get("key").asString(),
						j.get("url").asString(),
						j.get("contact").asString(),
						new PersistentRegistry<>(pluginPersistence),
						new PersistentRegistry<>(userPersistence)
				);
			} catch (IOException e) {
				logger.log(Level.WARNING, "Failed to load application", e);
			}
			return null;
		}).collect(Collectors.toSet());

		return Collections.unmodifiableCollection(all);
	}
}
