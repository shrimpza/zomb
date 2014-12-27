package net.shrimpworks.zomb.api;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.application.ApplicationRegistry;

/**
 * Exposes an HTTP API for managing client applications.
 */
public class ApplicationAPIService implements Closeable {

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
					String path = httpExchange.getRequestURI().getPath();
					if (path.matches("/.+/.+")) {
						String key = path.split("/")[2]; // lol string splits again
						Application app = appRegistry.forKey(key);
						if (app == null) app = appRegistry.find(key);

						if (app == null) {
							respond(httpExchange, 404); // not found
						} else if (appRegistry.remove(app) != null) {
							respond(httpExchange, 200); // success
						} else {
							respond(httpExchange, 500); // failed to delete
						}
					} else {
						respond(httpExchange, 400);
					}

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
