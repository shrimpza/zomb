package net.shrimpworks.zomb.entities;

import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.plugin.CommandImpl;
import net.shrimpworks.zomb.entities.plugin.CommandRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.PluginImpl;
import net.shrimpworks.zomb.entities.user.UserImpl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class QueryTest {

	private Application app;

	@Before
	public void setup() {
		app = new ApplicationImpl("app", "abc", null, null);
		app.plugins().add(new PluginImpl("weather", null, new CommandRegistryImpl(), null, null));
		app.plugins().find("weather").commands().add(new CommandImpl("current", null, 1, null));
		app.plugins().find("weather").commands().add(new CommandImpl("tomorrow", null, 1, null));
		app.plugins().find("weather").commands().add(new CommandImpl("friday", null, 2, null));
		app.plugins().find("weather").commands().add(new CommandImpl("saturday", null, 0, "[a-zA-Z]+, [A-Z]{2}"));
		app.users().add(new UserImpl("bob"));
	}

	@Test
	public void queryTest() {
		// plugin: weather
		// command: current
		// args[0]: johannesburg
		String q = "weather current johannesburg";

		Query query = new QueryImpl(app, app.users().find("bob"), q);

		assertEquals(q, query.query());
		assertEquals(app.users().find("bob"), query.user());
		assertEquals(app.plugins().find("weather"), query.plugin());
		assertEquals(app.plugins().find("weather").commands().find("current"), query.command());
		assertEquals(1, query.args().size());
		assertEquals("johannesburg", query.args().get(0));
	}

	@Test
	public void argumentCountTest() {
		String q = "weather tomorrow johannesburg south africa";

		try {
			new QueryImpl(app, app.users().find("bob"), q);
			fail("Query parsing should fail (too many arguments)");
		} catch (IllegalArgumentException expected) {
			// expected
		}

		q = "weather tomorrow \"johannesburg south africa\"";
		Query query = new QueryImpl(app, app.users().find("bob"), q);

		assertEquals(app.plugins().find("weather").commands().find("tomorrow"), query.command());
		assertEquals(1, query.args().size());

		q = "weather friday \"johannesburg south africa\" afternoon";
		query = new QueryImpl(app, app.users().find("bob"), q);

		assertEquals(app.plugins().find("weather").commands().find("friday"), query.command());
		assertEquals(2, query.args().size());
		assertEquals(query.args().get(0), "johannesburg south africa");
		assertEquals(query.args().get(1), "afternoon");
	}

	@Test
	public void argumentPatternTest() {
		// plugin: weather
		// command: saturday
		// args[0]: johannesburg, ZA
		String q = "weather saturday johannesburg, south africa";

		try {
			new QueryImpl(app, app.users().find("bob"), q);
			fail("Query parsing should fail (pattern mismatch)");
		} catch (IllegalArgumentException expected) {
			// expected
		}

		q = "weather saturday johannesburg, ZA";
		Query query = new QueryImpl(app, app.users().find("bob"), q);

		assertEquals(app.plugins().find("weather").commands().find("saturday"), query.command());
		assertEquals(1, query.args().size());
	}

	@Test
	public void pluginCommandValidationTest() {
		String q = "math add 1 1";

		try {
			new QueryImpl(app, app.users().find("bob"), q);
			fail("Query parsing should fail (no plugin named math)");
		} catch (IllegalArgumentException expected) {
			// expected
		}

		q = "weather yesterday johannesburg";
		try {
			new QueryImpl(app, app.users().find("bob"), q);
			fail("Query parsing should fail (no command named yesterday)");
		} catch (IllegalArgumentException expected) {
			// expected
		}
	}

}
