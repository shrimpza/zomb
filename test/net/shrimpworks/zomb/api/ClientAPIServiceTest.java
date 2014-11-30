package net.shrimpworks.zomb.api;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.shrimpworks.zomb.common.HttpClient;
import net.shrimpworks.zomb.entities.Query;
import net.shrimpworks.zomb.entities.Response;
import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.application.ApplicationRegistry;
import net.shrimpworks.zomb.entities.application.ApplicationRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.CommandImpl;
import net.shrimpworks.zomb.entities.plugin.CommandRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.plugin.PluginImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

		String pluginList = client.post(String.format("http://localhost:%d", port), "{" +
				"  \"key\": \"ckey\"," +
				"  \"user\": \"jane\"," +
				"  \"query\": \"plugin list\"" +
				"}");

		assertTrue(pluginList.contains("plugin"));
		assertTrue(pluginList.contains("help"));
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
					return list(query.application());
				case "add":
					return add(query);
				case "remove":
					return remove(query);
				default:
					throw new IllegalArgumentException("Unknown plugin command");
			}
		}

		private Response list(Application application) {
			return null;
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
}
