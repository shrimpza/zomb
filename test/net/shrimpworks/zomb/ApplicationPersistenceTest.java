package net.shrimpworks.zomb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.plugin.CommandImpl;
import net.shrimpworks.zomb.entities.plugin.CommandRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.PluginImpl;
import net.shrimpworks.zomb.entities.user.UserImpl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApplicationPersistenceTest {

	@Test
	public void applicationPersistenceTest() {
		Application application = new ApplicationImpl("app", "key", "http://url.com", "bob <bob@mail");

		application.users().add(new UserImpl("bob"));
		application.users().add(new UserImpl("jane"));

		application.plugins().add(new PluginImpl("weather", "weather info", new CommandRegistryImpl(), "http://plugin.url", "joe@mail"));
		application.plugins().find("weather").commands().add(new CommandImpl("current", "current weather", 1, ""));
		application.plugins().find("weather").commands().add(new CommandImpl("tomorrow", "tomorrow's weather", 0, null));

		application.plugins().add(new PluginImpl("math", "math ops", new CommandRegistryImpl(), "http://math.url", "sue@mail"));
		application.plugins().find("math").commands().add(new CommandImpl("add", "add numbers", 0, ""));

		ApplicationPersistence persistence = new ApplicationPersistence();
		persistence.save(application);

		List<Application> all = new ArrayList<>(persistence.all());

		assertEquals(1, all.size());
		assertEquals(application.name(), all.get(0).name());
		assertEquals(application.key(), all.get(0).key());
		assertEquals(application.url(), all.get(0).url());
		assertEquals(application.contact(), all.get(0).contact());
	}

	public static class ApplicationPersistence implements Persistence<Application> {

		@Override
		public boolean save(Application entity) {
			throw new UnsupportedOperationException("Method not implemented.");
		}

		@Override
		public boolean delete(Application entity) {
			throw new UnsupportedOperationException("Method not implemented.");
		}

		@Override
		public Collection<Application> all() {
			throw new UnsupportedOperationException("Method not implemented.");
		}
	}
}
