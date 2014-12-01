package net.shrimpworks.zomb.api;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.eclipsesource.json.JsonObject;
import net.shrimpworks.zomb.common.HttpClient;
import net.shrimpworks.zomb.entities.Query;
import net.shrimpworks.zomb.entities.Response;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.application.ApplicationRegistry;
import net.shrimpworks.zomb.entities.application.ApplicationRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.CommandImpl;
import net.shrimpworks.zomb.entities.plugin.CommandRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.plugin.PluginImpl;
import net.shrimpworks.zomb.entities.user.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ClientAPIServiceTest {

	private static final String host = "0.0.0.0";
	private static final int port = 8090;

	private ApplicationRegistry appRegistry;
	private ClientAPIService service;

	@Before
	public void setup() throws IOException {
		this.appRegistry = new ApplicationRegistryImpl();

		Set<ClientQueryExecutor> executors = new HashSet<>();
		executors.add(new PluginManagementExecutor());
		executors.add(new HelpExecutor());
		executors.add(new HttpExecutor());

		this.service = new ClientAPIServiceImpl(host, port, appRegistry, executors);
	}

	@After
	public void teardown() throws IOException {
		this.service.close();
	}

	@Test
	public void queryTest() throws IOException {
		this.appRegistry.add(new ApplicationImpl("client", "ckey", null, null));
		this.appRegistry.find("client").plugins().add(new PluginManager());
		this.appRegistry.find("client").plugins().add(new PluginImpl("help", "Provides plugin and command help functions",
				new CommandRegistryImpl(), null, null));


		HttpClient client = new HttpClient(1000);

		String apiUrl = String.format("http://localhost:%d", port);

		try {
			client.get(apiUrl);
			fail("GET not supported");
		} catch (IOException expected) {
			// expected
		}

		String pluginList = client.post(String.format("http://localhost:%d", port),
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

	public static class PluginManager extends PluginImpl {

		public PluginManager() {
			super("plugin", "Provides plugin management functionality", new CommandRegistryImpl(), null, null);
			commands().add(new CommandImpl("list", "list installed plugins", 0, null));
			commands().add(new CommandImpl("add", "add a new plugin", 1, null));
			commands().add(new CommandImpl("remove", "remove an installed plugin", 1, null));
		}
	}

	public static class PluginManagementExecutor implements ClientQueryExecutor {

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
			return null;
		}

		private Response remove(Query query) {
			return null;
		}
	}

	public static class HelpExecutor implements ClientQueryExecutor {

		@Override
		public boolean canExecute(Plugin plugin) {
			return plugin.name().equals("help");
		}

		@Override
		public Response execute(Query query) {
			throw new UnsupportedOperationException("Method not implemented.");
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

	public static class ResponseImpl implements Response {

		private final Plugin plugin;
		private final User user;
		private final String query;
		private final String[] response;
		private final String image;

		public ResponseImpl(Plugin plugin, User user, String query, String[] response, String image) {
			this.plugin = plugin;
			this.user = user;
			this.query = query;
			this.response = response;
			this.image = image;
		}

		@Override
		public Plugin plugin() {
			return plugin;
		}

		@Override
		public User user() {
			return user;
		}

		@Override
		public String query() {
			return query;
		}

		@Override
		public String[] response() {
			return response.clone();
		}

		@Override
		public String image() {
			return image;
		}
	}
}
