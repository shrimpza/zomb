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
import net.shrimpworks.zomb.entities.RegistryImpl;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.user.User;

/**
 * Implementation backed by JSON storage for managing Application persistence.
 */
public class ApplicationPersistence implements Persistence<Application> {

	private static final Logger logger = Logger.getLogger(ApplicationPersistence.class.getName());

	private final Persistence<JsonObject> persistence;
	private final ApplicationPersistenceFactory applicationPersistenceFactory;

	public ApplicationPersistence(Persistence<JsonObject> persistence, ApplicationPersistenceFactory applicationPersistenceFactory) {
		this.persistence = persistence;
		this.applicationPersistenceFactory = applicationPersistenceFactory;
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
						applicationPersistenceFactory != null
								? new PersistentRegistry<>(applicationPersistenceFactory.pluginPersistence(j.get("name").asString()))
								: new RegistryImpl<>(),
						applicationPersistenceFactory != null
								? new PersistentRegistry<>(applicationPersistenceFactory.userPersistence(j.get("name").asString()))
								: new RegistryImpl<>()
				);
			} catch (IOException e) {
				logger.log(Level.WARNING, "Failed to load application", e);
			}
			return null;
		}).collect(Collectors.toSet());

		return Collections.unmodifiableCollection(all);
	}

	public interface ApplicationPersistenceFactory {

		public Persistence<Plugin> pluginPersistence(String application) throws IOException;

		public Persistence<User> userPersistence(String application) throws IOException;
	}
}
