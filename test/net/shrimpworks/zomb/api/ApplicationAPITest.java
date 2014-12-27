package net.shrimpworks.zomb.api;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.shrimpworks.zomb.common.HttpClient;
import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.application.ApplicationRegistry;
import net.shrimpworks.zomb.entities.application.ApplicationRegistryImpl;
import net.shrimpworks.zomb.entities.application.NopApplicationPersistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationAPITest {

	private static final String host = "0.0.0.0";
	private static final int port = 8090;

	private ApplicationRegistry appRegistry;
	private ApplicationAPIService service;
	private HttpClient client;
	private String apiUrl;

	@Before
	public void setup() throws IOException {
		this.appRegistry = new ApplicationRegistryImpl(new NopApplicationPersistence());

		this.appRegistry.add(new ApplicationImpl("client", "ckey", null, null));
		this.appRegistry.find("client").plugins().add(new PluginManager());
		this.appRegistry.find("client").plugins().add(new Help());

		this.service = new ApplicationAPIService(host, port, appRegistry);

		this.client = new HttpClient(1000);

		this.apiUrl = String.format("http://localhost:%d/applications", port);
	}

	@After
	public void teardown() throws IOException {
		this.service.close();
	}

	@Test
	public void testAppAPI() throws IOException {
		// add an application, verify result
		JsonObject newApp = new JsonObject()
				.add("name", "test-app")
				.add("url", "http://mysite.com")
				.add("contact", "bob");
		JsonObject res = JsonObject.readFrom(client.post(apiUrl, newApp.toString()));
		assertFalse(res.get("key").isNull());
		assertFalse(res.get("key").asString().isEmpty());

		assertEquals(newApp.get("name").asString(), res.get("name").asString());
		assertFalse(res.get("plugins").asArray().isEmpty());
		assertTrue(res.get("users").asArray().isEmpty());

		// query application by key
		res = JsonObject.readFrom(client.get(String.format("%s/%s", apiUrl, res.get("key").asString()))); // get by key
		assertEquals(newApp.get("name").asString(), res.get("name").asString());
		assertFalse(res.get("plugins").asArray().isEmpty());
		assertTrue(res.get("users").asArray().isEmpty());

		// query application by name
		res = JsonObject.readFrom(client.get(String.format("%s/%s", apiUrl, newApp.get("name").asString())));
		assertEquals(newApp.get("name").asString(), res.get("name").asString());
		assertFalse(res.get("plugins").asArray().isEmpty());
		assertTrue(res.get("users").asArray().isEmpty());

		// request all applications
		JsonArray list = JsonArray.readFrom(client.get(apiUrl));
		assertFalse(list.isEmpty());
		assertEquals(2, list.size());
		Set<String> expectedApps = new HashSet<>(Arrays.asList("client", "test-app"));
		Set<String> foundApps = new HashSet<>();

		for (int i = 0; i < list.size(); i++) {
			foundApps.add(list.get(i).asObject().get("name").asString());
		}

		assertEquals(expectedApps, foundApps);

		// delete application
		client.delete(String.format("%s/%s", apiUrl, newApp.get("name").asString())); // delete by name
		client.delete(String.format("%s/%s", apiUrl, "ckey")); // delete by key

		// request all applications, ensuring deleted are gone
		list = JsonArray.readFrom(client.get(apiUrl));
		assertTrue(list.isEmpty());
	}

	public static class ApplicationAPIService implements Closeable {

		private static final Logger logger = Logger.getLogger(ClientAPIService.class.getName());

		private final HttpServer httpServer;
		private final ApplicationRegistry appRegistry;

		public ApplicationAPIService(String listenHost, int listenPort, ApplicationRegistry appRegistry) throws IOException {
			this.appRegistry = appRegistry;

			this.httpServer = HttpServer.create(new InetSocketAddress(listenHost, listenPort), 0);
			this.httpServer.createContext("/applications", new ApplicationHandler());
			this.httpServer.start();
		}

		@Override
		public void close() throws IOException {
			this.httpServer.stop(0);
		}

		private class ApplicationHandler extends HttpAPIHandler {

			@Override
			public void handle(HttpExchange httpExchange) throws IOException {
				try {
					httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // TODO config

					if (httpExchange.getRequestMethod().equals("POST")) {
						JsonObject req = JsonObject.readFrom(new InputStreamReader(httpExchange.getRequestBody()));

						Application app = new ApplicationImpl(
								req.get("name").asString(),
								UUID.randomUUID().toString(),
								req.get("url").asString(),
								req.get("contact").asString()
						);

						// add the default plugins
						app.plugins().add(new PluginManager());
						app.plugins().add(new Help());

						if (appRegistry.add(app)) {
							respond(httpExchange, 200, application(app).toString());
						} else {
							respond(httpExchange, 400);
						}

					} else if (httpExchange.getRequestMethod().equals("DELETE")) {

					} else if (httpExchange.getRequestMethod().equals("GET")) {
						String path = httpExchange.getRequestURI().getPath();
						if (path.matches("/.+/.+")) {
							String key = path.split("/")[2]; // lol string splits
							Application app = appRegistry.forKey(key);
							if (app == null) app = appRegistry.find(key);

							if (app != null) {
								respond(httpExchange, 200, application(app).toString());
							} else {
								respond(httpExchange, 404); // not found
							}
						} else {
							JsonArray apps = new JsonArray();
							appRegistry.all().forEach(a -> apps.add(application(a)));

							respond(httpExchange, 200, apps.toString());
						}

					} else {
						respond(httpExchange, 405); // method not allowed
					}
				} catch (Throwable t) {
					// HttpServer does not behave well when exceptions are thrown in handlers
					logger.log(Level.WARNING, t.getMessage(), t);

					respond(httpExchange, 500);
				} finally {
					httpExchange.close();
				}
			}

			/**
			 * Returns a JSON representation of the application passed in.
			 *
			 * @param application application to JSONify.
			 * @return JSON representation of application entity.
			 */
			private JsonObject application(Application application) {
				JsonArray plugins = new JsonArray();
				application.plugins().all().forEach(p -> plugins.add(p.name()));

				JsonArray users = new JsonArray();
				application.users().all().forEach(u -> users.add(u.name()));

				return new JsonObject()
						.add("key", application.key())
						.add("name", application.name())
						.add("url", application.url())
						.add("contact", application.contact())
						.add("plugins", plugins)
						.add("users", users);
			}

		}
	}
}
