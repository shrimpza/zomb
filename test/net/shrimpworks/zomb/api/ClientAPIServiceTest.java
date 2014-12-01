package net.shrimpworks.zomb.api;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import net.jadler.Jadler;
import net.shrimpworks.zomb.common.HttpClient;
import net.shrimpworks.zomb.entities.Query;
import net.shrimpworks.zomb.entities.Response;
import net.shrimpworks.zomb.entities.ResponseImpl;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.application.ApplicationRegistry;
import net.shrimpworks.zomb.entities.application.ApplicationRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.Command;
import net.shrimpworks.zomb.entities.plugin.CommandImpl;
import net.shrimpworks.zomb.entities.plugin.CommandRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.plugin.PluginImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ClientAPIServiceTest {

	private static final String host = "0.0.0.0";
	private static final int port = 8090;
	private static final int jadlerPort = 8092;

	private ApplicationRegistry appRegistry;
	private ClientAPIService service;
	private HttpClient client;
	private String apiUrl;

	@Before
	public void setup() throws IOException {
		this.appRegistry = new ApplicationRegistryImpl();

		this.appRegistry.add(new ApplicationImpl("client", "ckey", null, null));
		this.appRegistry.find("client").plugins().add(new PluginManager());
		this.appRegistry.find("client").plugins().add(new Help());

		Set<ClientQueryExecutor> executors = new HashSet<>();
		executors.add(new PluginManagementExecutor());
		executors.add(new HelpExecutor());
		executors.add(new HttpExecutor());

		this.service = new ClientAPIServiceImpl(host, port, appRegistry, executors);

		this.client = new HttpClient(1000);

		this.apiUrl = String.format("http://localhost:%d", port);

		Jadler.initJadlerListeningOn(8092);
	}

	@After
	public void teardown() throws IOException {
		this.service.close();

		Jadler.closeJadler();
	}

	@Test
	public void queryTest() throws IOException {
		try {
			client.get(apiUrl);
			fail("GET not supported");
		} catch (IOException expected) {
			// expected
		}

		try {
			client.post(apiUrl,
					new JsonObject()
							.add("key", "wrong-key")
							.add("user", "jane")
							.add("query", "plugin list")
							.toString());
			fail("Invalid key should fail");
		} catch (IOException expected) {
			// expected
		}

		try {
			client.post(apiUrl,
					new JsonObject()
							.add("key", "ckey")
							.add("user", "jane")
							.add("query", "wtf unknown")
							.toString());
			fail("Invalid plugin should fail");
		} catch (IOException expected) {
			// expected
		}

		String pluginList = client.post(apiUrl,
				new JsonObject()
						.add("key", "ckey")
						.add("user", "jane")
						.add("query", "plugin list")
						.toString());

		JsonObject json = JsonObject.readFrom(pluginList);

		assertEquals("jane", json.get("user").asString());
		assertEquals("plugin", json.get("plugin").asString());
		assertTrue(json.get("response").asArray().get(0).asString().contains("plugin"));
		assertTrue(json.get("response").asArray().get(0).asString().contains("help"));
	}

	@Test
	public void pluginManagerTest() throws IOException {

		Jadler.onRequest()
				.havingPathEqualTo("/hello")
				.havingMethodEqualTo("GET")
				.respond()
				.withContentType("application/json")
				.withStatus(200)
				.withBody(
						new JsonObject()
								.add("plugin", "hello")
								.add("help", "always says hello")
								.add("contact", "you <you@mail>")
								.add("commands", new JsonArray().add(
										new JsonObject()
												.add("command", "lol")
												.add("help", "says hello")
												.add("args", 0)
												.add("pattern", "")
								)).toString()
				);

		/*
		 * add a plugin
		 */
		String addPlugin = client.post(apiUrl,
				new JsonObject()
						.add("key", "ckey")
						.add("user", "jane")
						.add("query", String.format("plugin add http://localhost:%d/hello", jadlerPort))
						.toString()
		);

		JsonObject json = JsonObject.readFrom(addPlugin);

		assertEquals("jane", json.get("user").asString());
		assertEquals("plugin", json.get("plugin").asString());
		assertTrue(json.get("response").asArray().get(0).asString().toLowerCase().contains("success"));


		/*
		 * verify plugin was added
		 */
		String pluginList = client.post(apiUrl,
				new JsonObject()
						.add("key", "ckey")
						.add("user", "jane")
						.add("query", "plugin list")
						.toString());

		json = JsonObject.readFrom(pluginList);

		assertEquals("jane", json.get("user").asString());
		assertEquals("plugin", json.get("plugin").asString());
		assertTrue(json.get("response").asArray().get(0).asString().contains("hello"));


		/*
		 * remove plugin
		 */
		String removePlugin = client.post(apiUrl,
				new JsonObject()
						.add("key", "ckey")
						.add("user", "jane")
						.add("query", "plugin remove hello")
						.toString()
		);

		json = JsonObject.readFrom(removePlugin);

		assertEquals("jane", json.get("user").asString());
		assertEquals("plugin", json.get("plugin").asString());
		assertTrue(json.get("response").asArray().get(0).asString().toLowerCase().contains("success"));


		/*
		 * verify plugin was removed
		 */
		pluginList = client.post(apiUrl,
				new JsonObject()
						.add("key", "ckey")
						.add("user", "jane")
						.add("query", "plugin list")
						.toString());

		json = JsonObject.readFrom(pluginList);

		assertEquals("jane", json.get("user").asString());
		assertEquals("plugin", json.get("plugin").asString());
		assertFalse(json.get("response").asArray().get(0).asString().contains("hello"));
	}

	@Test
	public void helpTest() throws IOException {
		/*
		 * get help for the "plugin" plugin
		 */
		String pluginHelp = client.post(apiUrl,
				new JsonObject()
						.add("key", "ckey")
						.add("user", "jane")
						.add("query", "help show plugin")
						.toString());

		JsonObject json = JsonObject.readFrom(pluginHelp);

		assertEquals("jane", json.get("user").asString());
		assertEquals("help", json.get("plugin").asString());
		assertTrue(json.get("response").asArray().get(0).asString().equals("Provides plugin management functionality"));


		/*
		 * get list of commands for plugin management
		 */
		String pluginCmdList = client.post(apiUrl,
				new JsonObject()
						.add("key", "ckey")
						.add("user", "jane")
						.add("query", "help list plugin")
						.toString());

		json = JsonObject.readFrom(pluginCmdList);

		assertEquals("jane", json.get("user").asString());
		assertEquals("help", json.get("plugin").asString());
		assertTrue(json.get("response").asArray().get(0).asString().contains("list"));
		assertTrue(json.get("response").asArray().get(0).asString().contains("add"));
		assertTrue(json.get("response").asArray().get(0).asString().contains("remove"));
	}

	public static class PluginManager extends PluginImpl {

		public PluginManager() {
			super("plugin", "Provides plugin management functionality", new CommandRegistryImpl(), null, null);
			commands().add(new CommandImpl("list", "list installed plugins", 0, null));
			commands().add(new CommandImpl("add", "add a new plugin", 1, null));
			commands().add(new CommandImpl("remove", "remove an installed plugin", 1, null));
		}
	}

	public static class PluginManagementExecutor implements ClientQueryExecutor {

		private static final Logger logger = Logger.getLogger(PluginManagementExecutor.class.getName());

		private final HttpClient client;

		public PluginManagementExecutor() {
			this.client = new HttpClient(5000); // TODO config
		}

		@Override
		public boolean canExecute(Plugin plugin) {
			return plugin instanceof PluginManager;
		}

		@Override
		public Response execute(Query query) {
			switch (query.command().name()) {
				case "list":
					return list(query);
				case "add":
					return add(query);
				case "remove":
					return remove(query);
				default:
					throw new IllegalArgumentException("Unknown plugin command");
			}
		}

		private Response list(Query query) {
			StringBuilder sb = new StringBuilder();
			Iterator<Plugin> plugins = query.application().plugins().all().iterator();
			while (plugins.hasNext()) {
				sb.append(plugins.next().name());
				if (plugins.hasNext()) {
					sb.append(", ");
				}
			}

			return new ResponseImpl(query.plugin(), query.user(), query.query(), new String[]{sb.toString()}, null);
		}

		private Response add(Query query) {
			try {
				String pluginDefSource = client.get(query.args().get(0));

				JsonObject pluginDef = JsonObject.readFrom(pluginDefSource);

				if (query.application().plugins().find(pluginDef.get("plugin").asString()) != null) {
					return new ResponseImpl(query, new String[]{"Failed to add plugin: already exists"}, null);
				}

				if (pluginDef.get("commands").asArray().isEmpty()) {
					return new ResponseImpl(query, new String[]{"Failed to add plugin: no commands"}, null);
				}

				Plugin plugin = new PluginImpl(
						pluginDef.get("plugin").asString(),
						pluginDef.get("help").asString(),
						new CommandRegistryImpl(),
						query.args().get(0),
						pluginDef.get("contact").asString());

				for (JsonValue commands : pluginDef.get("commands").asArray()) {
					plugin.commands().add(new CommandImpl(
							commands.asObject().get("command").asString(),
							commands.asObject().get("help").asString(),
							commands.asObject().get("args").asInt(),
							commands.asObject().get("pattern").asString()
					));
				}

				if (query.application().plugins().add(plugin)) {
					return new ResponseImpl(query, new String[]{String.format("Success, added new plugin %s", plugin.name())}, null);
				} else {
					return new ResponseImpl(query, new String[]{"Failed to add plugin to application"}, null);
				}
			} catch (IOException e) {
				logger.log(Level.WARNING, "Failed to get plugin definition", e);

				return new ResponseImpl(query, new String[]{"Failed to get plugin: " + e.getMessage()}, null);
			}
		}

		private Response remove(Query query) {
			Plugin plugin = query.application().plugins().find(query.args().get(0));
			if (plugin != null) {
				if (plugin == query.application().plugins().remove(plugin)) {
					return new ResponseImpl(query, new String[]{"Success, removed plugin"}, null);
				} else {
					return new ResponseImpl(query, new String[]{"Failed to remove plugin from application"}, null);
				}
			} else {
				return new ResponseImpl(query, new String[]{"Failed to remove plugin, does not exist"}, null);
			}
		}
	}

	public static class Help extends PluginImpl {

		public Help() {
			super("help", "Provides plugin and command help functions", new CommandRegistryImpl(), null, null);
			commands().add(new CommandImpl("show", "shows help for a plugin or plugin command", 0, "[A-Za-z]+|[A-Za-z]+ [A-Za-z]+"));
			commands().add(new CommandImpl("list", "list commands available in a plugin", 1, null));
		}
	}

	public static class HelpExecutor implements ClientQueryExecutor {

		@Override
		public boolean canExecute(Plugin plugin) {
			return plugin instanceof Help;
		}

		@Override
		public Response execute(Query query) {
			switch (query.command().name()) {
				case "show":
					return show(query);
				case "list":
					return list(query);
				default:
					throw new IllegalArgumentException("Unknown plugin command");
			}
		}

		private Response show(Query query) {
			if (query.args().size() > 1) throw new UnsupportedOperationException("Not implemented"); // TODO

			Plugin plugin = query.application().plugins().find(query.args().get(0));
			return new ResponseImpl(query, new String[]{plugin.help()}, null);
		}

		private Response list(Query query) {
			Plugin plugin = query.application().plugins().find(query.args().get(0));

			StringBuilder sb = new StringBuilder();
			Iterator<Command> commands = plugin.commands().all().iterator();
			while (commands.hasNext()) {
				sb.append(commands.next().name());
				if (commands.hasNext()) {
					sb.append(", ");
				}
			}

			return new ResponseImpl(query, new String[]{sb.toString()}, null);
		}
	}

	public static class HttpExecutor implements ClientQueryExecutor {

		@Override
		public boolean canExecute(Plugin plugin) {
			return plugin.url() != null && plugin.url().matches("http://.+");
		}

		@Override
		public Response execute(Query query) {
			throw new UnsupportedOperationException("Method not implemented.");
		}
	}

}
