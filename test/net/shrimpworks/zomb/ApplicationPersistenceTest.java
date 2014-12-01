package net.shrimpworks.zomb;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.plugin.CommandImpl;
import net.shrimpworks.zomb.entities.plugin.CommandRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.PluginImpl;
import net.shrimpworks.zomb.entities.user.UserImpl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApplicationPersistenceTest {

	@Test
	public void applicationPersistenceTest() throws IOException {
		Application application = new ApplicationImpl("app", "key", "http://url.com", "bob <bob@mail>");

		application.users().add(new UserImpl("bob"));
		application.users().add(new UserImpl("jane"));

		application.plugins().add(new PluginImpl("weather", "weather info", new CommandRegistryImpl(), "http://plugin.url", "joe@mail"));
		application.plugins().find("weather").commands().add(new CommandImpl("current", "current weather", 1, ""));
		application.plugins().find("weather").commands().add(new CommandImpl("tomorrow", "tomorrow's weather", 0, null));

		application.plugins().add(new PluginImpl("math", "math ops", new CommandRegistryImpl(), "http://math.url", "sue@mail"));
		application.plugins().find("math").commands().add(new CommandImpl("add", "add numbers", 0, ""));

		ApplicationPersistence persistence = new ApplicationPersistence(Files.createTempDirectory("zomb_apps"));
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

		assertNotNull(app.plugins().find("weather").commands().find("current"));
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
				os.writeUnshared(entity);
				os.flush();

				return true;
			}
		}

		@Override
		public boolean delete(Application entity) throws IOException {
			throw new UnsupportedOperationException("Method not implemented.");
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
