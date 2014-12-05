package net.shrimpworks.zomb;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import net.shrimpworks.zomb.entities.Persistence;
import net.shrimpworks.zomb.entities.PersistentRegistry;
import net.shrimpworks.zomb.entities.Registry;
import net.shrimpworks.zomb.entities.RegistryImpl;
import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.plugin.Command;
import net.shrimpworks.zomb.entities.plugin.CommandImpl;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.plugin.PluginImpl;
import net.shrimpworks.zomb.entities.user.User;
import net.shrimpworks.zomb.entities.user.UserImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApplicationPersistenceTest {

	private Path temp;

	@Before
	public void setup() throws IOException {
		temp = Files.createTempDirectory("zomb_apps");
	}

	@After
	public void teardown() throws IOException {
		Files.deleteIfExists(temp);
	}

	@Test
	public void persistentRegistryTest() throws IOException {
		Persistence<Application> appStore = new ApplicationPersistence(new FilesystemPersistence(temp));
		Persistence<Plugin> plugStore = new PluginPersistence(new FilesystemPersistence(temp.resolve("app.plugins")));
		Persistence<User> userStore = new UserPersistence(new FilesystemPersistence(temp.resolve("app.users")));

		PersistentRegistry<Application> appRegistry = new PersistentRegistry<>(appStore);
		PersistentRegistry<Plugin> pluginRegistry = new PersistentRegistry<>(plugStore);
		PersistentRegistry<User> userRegistry = new PersistentRegistry<>(userStore);

		Application application = new ApplicationImpl("app", "key", "http://url.com", "bob <bob@mail>", pluginRegistry, userRegistry);

		application.users().add(new UserImpl("bob"));
		application.users().add(new UserImpl("jane"));


		Plugin weather = new PluginImpl("weather", "weather info", "http://plugin.url", "joe@mail");
		weather.commands().add(new CommandImpl("current", "current weather", 1, ""));
		weather.commands().add(new CommandImpl("tomorrow", "tomorrow's weather", 0, null));
		application.plugins().add(weather);

		Plugin math = new PluginImpl("math", "math ops", "http://math.url", "sue@mail");
		math.commands().add(new CommandImpl("add", "add numbers", 0, ""));
		application.plugins().add(math);

		appRegistry.add(application);

		List<Application> all = new ArrayList<>(appStore.all());

		assertEquals(1, all.size());

		Application app = all.get(0);

		assertEquals(application.name(), app.name());
		assertEquals(application.key(), app.key());
		assertEquals(application.url(), app.url());
		assertEquals(application.contact(), app.contact());

		assertNotNull(application.users().find("bob"));
		assertNotNull(application.users().find("jane"));

		assertEquals(2, app.plugins().all().size());
		assertNotNull(app.plugins().find("weather"));
		assertNotNull(app.plugins().find("math"));

		assertEquals("joe@mail", app.plugins().find("weather").contact());

		assertNotNull(app.plugins().find("weather").commands().find("current"));

		assertNotNull(appRegistry.remove(application));

		assertEquals(0, appStore.all().size());
	}

	public static class ApplicationPersistence implements Persistence<Application> {

		private final Persistence<JsonObject> persistence;

		public ApplicationPersistence(Persistence<JsonObject> persistence) {
			this.persistence = persistence;
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

			Set<Application> all = json.stream().map(j -> new ApplicationImpl(
					j.get("name").asString(),
					j.get("key").asString(),
					j.get("url").asString(),
					j.get("contact").asString()
			)).collect(Collectors.toSet());

			return Collections.unmodifiableCollection(all);
		}
	}

	public static class UserPersistence implements Persistence<User> {

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


	public static class PluginPersistence implements Persistence<Plugin> {

		private final Persistence<JsonObject> persistence;

		public PluginPersistence(Persistence<JsonObject> persistence) {
			this.persistence = persistence;
		}

		@Override
		public boolean save(Plugin entity) throws IOException {
			JsonObject json = new JsonObject()
					.add("name", entity.name())
					.add("help", entity.name())
					.add("url", entity.name())
					.add("contact", entity.name());
			return persistence.save(json);
		}

		@Override
		public boolean delete(Plugin entity) throws IOException {
			return persistence.delete(new JsonObject().add("name", entity.name()));
		}

		@Override
		public Collection<Plugin> all() throws IOException {
			Collection<JsonObject> json = persistence.all();

			Set<Plugin> all = json.stream().map(j -> new PluginImpl(
					j.get("name").asString(),
					j.get("help").asString(),
					j.get("url").asString(),
					j.get("contact").asString(),
					commands(j.get("commands").asArray())
			)).collect(Collectors.toSet());

			return Collections.unmodifiableCollection(all);
		}

		private Registry<Command> commands(JsonArray jsonArray) {
			Registry<Command> commands = new RegistryImpl<>();
			jsonArray.forEach(j -> commands.add(new CommandImpl(
							j.asObject().get("name").asString(),
							j.asObject().get("help").asString(),
							j.asObject().get("args").asInt(),
							j.asObject().get("pattern").asString()
					))
			);
			return commands;
		}
	}

	public static class FilesystemPersistence implements Persistence<JsonObject> {

		private static final Logger logger = Logger.getLogger(FilesystemPersistence.class.getName());

		private final Path path;

		public FilesystemPersistence(Path path) throws IOException {
			this.path = Files.createDirectories(path);
		}

		@Override
		public boolean save(JsonObject entity) throws IOException {
			return Files.write(path.resolve(entity.get("name").asString()), entity.toString().getBytes(Charset.forName("UTF-8"))) != null;
		}

		@Override
		public boolean delete(JsonObject entity) throws IOException {
			return Files.deleteIfExists(path.resolve(entity.get("name").asString()));
		}

		@Override
		public Collection<JsonObject> all() throws IOException {
			Set<JsonObject> all = new HashSet<>();
			Files.list(path).filter(Files::isRegularFile).forEach((p) -> {
				try (Reader r = Files.newBufferedReader(p)) {
					all.add(JsonObject.readFrom(r));
				} catch (IOException e) {
					logger.log(Level.WARNING, "Could not read JSON file", e);
				}
			});
			return all;
		}
	}
}
