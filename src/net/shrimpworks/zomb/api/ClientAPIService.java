package net.shrimpworks.zomb.api;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.shrimpworks.zomb.entities.Query;
import net.shrimpworks.zomb.entities.QueryImpl;
import net.shrimpworks.zomb.entities.Response;
import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationRegistry;
import net.shrimpworks.zomb.entities.user.User;
import net.shrimpworks.zomb.entities.user.UserImpl;

public class ClientAPIService implements Closeable {

	private static final Logger logger = Logger.getLogger(ClientAPIService.class.getName());

	private final HttpServer httpServer;
	private final ApplicationRegistry appRegistry;
	private final Set<ClientQueryExecutor> executors;

	public ClientAPIService(String listenHost, int listenPort, ApplicationRegistry appRegistry,
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

	private class RootHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			try {
				if (!httpExchange.getRequestMethod().equals("POST")) {
					respond(httpExchange, 405); // method not allowed - only POST supported
				} else {
					JsonObject req = JsonObject.readFrom(new InputStreamReader(httpExchange.getRequestBody()));

					Application application = appRegistry.forKey(req.get("key").asString());
					if (application == null) {
						respond(httpExchange, 403); // forbidden - invalid application
						return;
					}

					User user = application.users().find(req.get("user").asString());
					if (user == null) application.users().add(user = new UserImpl(req.get("user").asString()));

					try {
						Query query = new QueryImpl(application, user, req.get("query").asString());
						Response response = null;

						for (ClientQueryExecutor executor : executors) {
							if (executor.canExecute(query.plugin())) {
								response = executor.execute(query);
							}
						}

						if (response != null) {
							respond(httpExchange, 200, jsonResponse(response).toString()); // OK
						} else {
							respond(httpExchange, 501); // not implemented - no executors for found plugin
						}

					} catch (IllegalArgumentException e) {
						logger.log(Level.INFO, e.getMessage(), e);

						respond(httpExchange, 400); // bad request - invalid query
					}
				}
			} catch (Throwable t) {
				// HttpServer does not behave well when exceptions are thrown in handlers
				logger.log(Level.WARNING, t.getMessage(), t);

				respond(httpExchange, 500);
			} finally {
				httpExchange.close();
			}
		}

		private void respond(HttpExchange exchange, int status) throws IOException {
			respond(exchange, status, null);
		}

		private void respond(HttpExchange exchange, int status, String body) throws IOException {
			exchange.sendResponseHeaders(status, body == null || body.isEmpty() ? -1 : body.length());
			if (body != null && !body.isEmpty()) exchange.getResponseBody().write(body.getBytes());
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
					.add("image", response.image() == null ? "" : response.image());
		}
	}
}
