package net.shrimpworks.zomb.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.plugin.Command;
import net.shrimpworks.zomb.entities.plugin.CommandImpl;
import net.shrimpworks.zomb.entities.plugin.CommandRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.plugin.PluginImpl;
import net.shrimpworks.zomb.entities.user.User;
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
		// plugin: weather
		// command: tomorrow
		// args[0]: johannesburg south africa
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

	public static class QueryImpl implements Query {

		private final Application application;
		private final User user;
		private final Plugin plugin;
		private final Command command;
		private final List<String> args;
		private final String query;

		public QueryImpl(Application application, User user, String query) {
			this.application = application;
			this.user = user;
			this.query = query;

			List<String> queryParts = queryParts(query);

			if (queryParts.size() < 2) throw new IllegalArgumentException("Invalid query string");

			this.plugin = application.plugins().find(queryParts.remove(0));
			if (this.plugin == null) throw new IllegalArgumentException("Invalid query, plugin not found");

			this.command = this.plugin.commands().find(queryParts.remove(0));
			if (this.command == null) throw new IllegalArgumentException("Invalid query, command not found");

			if (this.command.pattern() != null) {
				String queryString = query.replaceFirst(this.plugin.name() + " " + this.command.name() + " ", "");
				if (!queryString.matches(this.command.pattern().pattern()))
					throw new IllegalArgumentException("Invalid query string, query does not match required format");

				this.args = Collections.singletonList(queryString);
			} else {
				if (this.command.arguments() > 0 && queryParts.size() != this.command.arguments())
					throw new IllegalArgumentException("Invalid query string, too many arguments");

				this.args = queryParts;
			}
		}

		@Override
		public Application application() {
			return application;
		}

		@Override
		public User user() {
			return user;
		}

		@Override
		public Plugin plugin() {
			return plugin;
		}

		@Override
		public Command command() {
			return command;
		}

		@Override
		public List<String> args() {
			return Collections.unmodifiableList(args);
		}

		@Override
		public String query() {
			return query;
		}

		private List<String> queryParts(String query) {
			List<String> res = new ArrayList<>();
			Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
			Matcher regexMatcher = regex.matcher(query);

			while (regexMatcher.find()) {
				if (regexMatcher.group(1) != null) {
					// Add double-quoted string without the quotes
					res.add(regexMatcher.group(1));
				} else if (regexMatcher.group(2) != null) {
					// Add single-quoted string without the quotes
					res.add(regexMatcher.group(2));
				} else {
					// Add unquoted word
					res.add(regexMatcher.group());
				}
			}

			return res;
		}
	}

}
