package net.shrimpworks.zomb.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.shrimpworks.zomb.common.HttpClient;
import net.shrimpworks.zomb.entities.Query;
import net.shrimpworks.zomb.entities.QueryImpl;
import net.shrimpworks.zomb.entities.Response;
import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.application.ApplicationRegistry;
import net.shrimpworks.zomb.entities.application.ApplicationRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.CommandRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.plugin.PluginImpl;
import net.shrimpworks.zomb.entities.user.User;
import net.shrimpworks.zomb.entities.user.UserImpl;

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
		this.appRegistry.find("client").plugins().add(new PluginImpl("plugin", "Provides plugin management functions",
				new CommandRegistryImpl(), null, null));
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

	public static class ClientAPIServiceImpl implements ClientAPIService {

		private final HttpServer httpServer;
		private final ApplicationRegistry appRegistry;
		private final Set<ClientQueryExecutor> executors;

		public ClientAPIServiceImpl(String listenHost, int listenPort, ApplicationRegistry appRegistry,
									Set<ClientQueryExecutor> executors) throws IOException {
			this.appRegistry = appRegistry;
			this.executors = executors;

			this.httpServer = HttpServer.create(new InetSocketAddress(listenHost, listenPort), 0);
			this.httpServer.createContext("/", new RootHandler());
			this.httpServer.start();
		}

		@Override
		public void close() throws IOException {
			this.httpServer.stop(0);
		}

		@Override
		public Set<ClientQueryExecutor> executors() {
			return Collections.unmodifiableSet(executors);
		}

		private class RootHandler implements HttpHandler {

			@Override
			public void handle(HttpExchange httpExchange) throws IOException {
				try {
					if (!httpExchange.getRequestMethod().equals("POST")) {
						httpExchange.sendResponseHeaders(405, -1); // method not allowed
					} else {
						JsonObject req = JsonObject.readFrom(new InputStreamReader(httpExchange.getRequestBody()));

						Application application = appRegistry.forKey(req.get("key").asString());
						if (application == null) {
							httpExchange.sendResponseHeaders(403, -1); // forbidden - invalid application
							return;
						}

						User user = application.users().find(req.get("user").asString());
						if (user == null) application.users().add(new UserImpl(req.get("user").asString()));

						try {
							Query query = new QueryImpl(application, user, req.get("query").asString());
							Response response = null;

							for (ClientQueryExecutor executor : executors) {
								if (executor.canExecute(query.plugin())) {
									response = executor.execute(query);
								}
							}

							if (response != null) {
								String jsonResponse = jsonResponse(response).asString();
								httpExchange.sendResponseHeaders(200, jsonResponse.length());
								httpExchange.getResponseBody().write(jsonResponse.getBytes());
							} else {
								httpExchange.sendResponseHeaders(501, -1); // not implemented - no executors for found plugin
							}

						} catch (IllegalArgumentException e) {
							httpExchange.sendResponseHeaders(400, -1); // bas request - invalid query
						}
					}
				} catch (Throwable t) {
					// HttpServer does not behave well when exceptions are thrown in handlers
					httpExchange.sendResponseHeaders(500, -1);
				}
			}

			private JsonObject jsonResponse(Response response) {
				JsonArray resStrings = new JsonArray();
				for (String s : response.response()) {
					resStrings.add(s);
				}

				return new JsonObject()
						.add("plugin", response.plugin().name())
						.add("user", response.user().name())
						.add("query", response.query())
						.add("response", resStrings)
						.add("image", response.image());
			}
		}
	}

	public static class PluginManagementExecutor implements ClientQueryExecutor {

		@Override
		public boolean canExecute(Plugin plugin) {
			return plugin.name().equals("plugin");
		}

		@Override
		public Response execute(Query query) {
			throw new UnsupportedOperationException("Method not implemented.");
		}

		private Response list() {
			return null;
		}

		private Response add() {
			return null;
		}

		private Response remove() {
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
