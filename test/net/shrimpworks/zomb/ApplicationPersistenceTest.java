package net.shrimpworks.zomb;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eclipsesource.json.JsonObject;
import net.shrimpworks.zomb.entities.AbstractRegistry;
import net.shrimpworks.zomb.entities.HasName;
import net.shrimpworks.zomb.entities.Registry;
import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.plugin.CommandImpl;
import net.shrimpworks.zomb.entities.plugin.CommandRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.PluginImpl;
import net.shrimpworks.zomb.entities.user.UserImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
	public void applicationPersistenceTest() throws IOException {
		Application application = new ApplicationImpl("app", "key", "http://url.com", "bob <bob@mail>");

		application.users().add(new UserImpl("bob"));
		application.users().add(new UserImpl("jane"));

		application.plugins().add(new PluginImpl("weather", "weather info", "http://plugin.url", "joe@mail", new CommandRegistryImpl()));
		application.plugins().find("weather").commands().add(new CommandImpl("current", "current weather", 1, ""));
		application.plugins().find("weather").commands().add(new CommandImpl("tomorrow", "tomorrow's weather", 0, null));

		application.plugins().add(new PluginImpl("math", "math ops", "http://math.url", "sue@mail", new CommandRegistryImpl()));
		application.plugins().find("math").commands().add(new CommandImpl("add", "add numbers", 0, ""));

		ApplicationPersistence persistence = new ApplicationPersistence(temp);
		persistence.save(application);

		List<Application> all = new ArrayList<>(persistence.all());

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

		assertTrue(persistence.delete(app));
	}

	@Test
	public void persistentRegistryTest() throws IOException {
		Persistence<JsonObject> appStore = new FilesystemPersistence(temp);
//		PersistentAppRegistry appRegistry = new PersistentAppRegistry(appStore);

		Application application = new ApplicationImpl("app", "key", "http://url.com", "bob <bob@mail>");

		application.users().add(new UserImpl("bob"));
		application.users().add(new UserImpl("jane"));

		application.plugins().add(new PluginImpl("weather", "weather info", "http://plugin.url", "joe@mail", new CommandRegistryImpl()));
		application.plugins().find("weather").commands().add(new CommandImpl("current", "current weather", 1, ""));
		application.plugins().find("weather").commands().add(new CommandImpl("tomorrow", "tomorrow's weather", 0, null));

		application.plugins().add(new PluginImpl("math", "math ops", "http://math.url", "sue@mail", new CommandRegistryImpl()));
		application.plugins().find("math").commands().add(new CommandImpl("add", "add numbers", 0, ""));

//		appRegistry.add(application);

		fail("todo");
	}

	public static class PersistentAppRegistry extends PersistentRegistry<Application> {

		public PersistentAppRegistry(Persistence<Application> persistence) throws IOException {
			super(persistence);
		}
	}

	public static class PersistentRegistry<T extends HasName> extends AbstractRegistry<T> implements Registry<T>, Serializable {

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

	public static class FilesystemPersistence implements Persistence<JsonObject> {

		private static final Logger logger = Logger.getLogger(FilesystemPersistence.class.getName());

		private final Path path;

		public FilesystemPersistence(Path path) {
			this.path = path;
		}

		@Override
		public boolean save(JsonObject entity) throws IOException {
			return Files.write(path.resolve(entity.get("name").asString()), entity.toString().getBytes(Charset.forName("UTF-8"))) != null;
		}

		@Override
		public boolean delete(JsonObject entity) throws IOException {
			throw new UnsupportedOperationException("Method not implemented.");
		}

		@Override
		public Collection<JsonObject> all() throws IOException {
			throw new UnsupportedOperationException("Method not implemented.");
		}

		//		@Override
//		public boolean save(T entity) throws IOException {
//			try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path.resolve(entity.name()).toFile()))) {
//				os.writeObject(entity);
//				os.flush();
//
//				return true;
//			}
//		}
//
//		@Override
//		public boolean delete(T entity) throws IOException {
//			return Files.deleteIfExists(path.resolve(entity.name()));
//		}
//
//		@Override
//		public Collection<T> all() throws IOException {
//			Set<T> all = new HashSet<>();
//
//			for (Object o : Files.list(path).toArray()) {
//				all.add(readFile((Path) o));
//			}
//
//			return all;
//		}
//
//		@SuppressWarnings("unchecked")
//		private T readFile(Path path) throws IOException {
//			try (ObjectInputStream is = new ObjectInputStream(Files.newInputStream(path))) {
//				return (T) is.readObject();
//			} catch (ClassNotFoundException e) {
//				logger.log(Level.WARNING, "Failed to read entity", e);
//			}
//			return null;
//		}
	}

	public static class ApplicationPersistence implements Persistence<Application> {

		private static final Logger logger = Logger.getLogger(ApplicationPersistence.class.getName());

		private final Path path;

		public ApplicationPersistence(Path path) {
			this.path = path;
		}

		@Override
		public boolean save(Application entity) throws IOException {
			try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path.resolve(entity.key()).toFile()))) {
				os.writeObject(entity);
				os.flush();

				return true;
			}
		}

		@Override
		public boolean delete(Application entity) throws IOException {
			return Files.deleteIfExists(path.resolve(entity.key()));
		}

		@Override
		public Collection<Application> all() throws IOException {
			Set<Application> all = new HashSet<>();

			for (Object o : Files.list(path).toArray()) {
				all.add(readFile((Path) o));
			}

			return all;
		}

		private Application readFile(Path path) throws IOException {
			try (ObjectInputStream is = new ObjectInputStream(Files.newInputStream(path))) {
				return (Application) is.readObject();
			} catch (ClassNotFoundException e) {
				logger.log(Level.WARNING, "Failed to read entity", e);
			}
			return null;
		}
	}
}
