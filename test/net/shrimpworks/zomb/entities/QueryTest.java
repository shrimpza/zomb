package net.shrimpworks.zomb.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.plugin.Command;
import net.shrimpworks.zomb.entities.plugin.CommandRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.plugin.PluginImpl;
import net.shrimpworks.zomb.entities.user.User;
import net.shrimpworks.zomb.entities.user.UserImpl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QueryTest {

	@Test
	public void queryTest() {
		// plugin: weather
		// command: current
		// args[0]: johannesburg
		String q = "weather current johannesburg";

		Application app = new ApplicationImpl("app", "abc", null, null);
		app.plugins().add(new PluginImpl("weather", null, new CommandRegistryImpl(), null, null));
		app.plugins().find("weather").commands().add(new CommandImpl("current", null, 1, null));
		app.users().add(new UserImpl("bob"));

		Query query = new QueryImpl(app, app.users().find("bob"), q);

		assertEquals(q, query.query());
		assertEquals(app.users().find("bob"), query.user());
		assertEquals(app.plugins().find("weather"), query.plugin());
		assertEquals(app.plugins().find("weather").commands().find("current"), query.command());
		assertEquals(1, query.args().size());
		assertEquals("johannesburg", query.args().get(0));
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

			// TODO parse query

			this.plugin = application.plugins().find("PARSED-PLUGIN"); // TODO
			this.command = this.plugin.commands().find("PARSED-COMMAND"); // TODO
			this.args = new ArrayList<>(); // TODO
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
	}

	public static class CommandImpl implements Command {

		private final String name;
		private final String help;
		private final int args;
		private final Pattern pattern;

		public CommandImpl(String name, String help, int args, String pattern) {
			this.name = name;
			this.help = help;
			this.args = args;
			this.pattern = pattern != null && !pattern.isEmpty() ? Pattern.compile(pattern) : null;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public String help() {
			return help;
		}

		@Override
		public int arguments() {
			return args;
		}

		@Override
		public Pattern pattern() {
			return pattern;
		}
	}
}
