package net.shrimpworks.zomb.entities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationFilesystemPersistenceFactory;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.application.ApplicationPersistence;
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

public class PersistenceTest {

	private final String appName = "app";

	private Path temp;

	@Before
	public void setup() throws IOException {
		temp = Files.createTempDirectory("zomb_apps");
	}

	@After
	public void teardown() throws IOException, InterruptedException {
		Files.deleteIfExists(temp.resolve(appName + ".plugins"));
		Files.deleteIfExists(temp.resolve(appName + ".users"));
		Files.deleteIfExists(temp);
	}

	@Test
	public void persistentRegistryTest() throws IOException {

		ApplicationPersistence.ApplicationPersistenceFactory appPersistence = new ApplicationFilesystemPersistenceFactory(temp);
		Persistence<Application> appStore = new ApplicationPersistence(new FilesystemPersistence(temp), appPersistence);

		PersistentRegistry<Application> appRegistry = new PersistentRegistry<>(appStore);
		PersistentRegistry<Plugin> pluginRegistry = new PersistentRegistry<>(appPersistence.pluginPersistence(appName));
		PersistentRegistry<User> userRegistry = new PersistentRegistry<>(appPersistence.userPersistence(appName));

		Application application = new ApplicationImpl(appName, "key", "http://url.com", "bob <bob@mail>", pluginRegistry, userRegistry);

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

		// clean up bits
		assertNotNull(app.plugins().remove(app.plugins().find("weather")));
		assertNotNull(app.plugins().remove(app.plugins().find("math")));
		assertNotNull(app.users().remove(app.users().find("bob")));
		assertNotNull(app.users().remove(app.users().find("jane")));
	}


}
