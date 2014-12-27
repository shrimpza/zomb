package net.shrimpworks.zomb.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eclipsesource.json.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationRegistry;

import org.junit.Test;

import static org.junit.Assert.fail;

public class ApplicationAPITest {

	@Test
	public void testAppAPI() {
		// TODO add an application, verify result

		// TODO query application by key

		// TODO query application by name

		// TODO request all applications

		// TODO delete application

		// TODO request all applications, ensuring deleted is gone

		fail("todo");
	}

	public static class ApplicationAPIService {

		private static final Logger logger = Logger.getLogger(ClientAPIService.class.getName());

		private final HttpServer httpServer;
		private final ApplicationRegistry appRegistry;

		public ApplicationAPIService(String listenHost, int listenPort, ApplicationRegistry appRegistry) throws IOException {
			this.appRegistry = appRegistry;

			this.httpServer = HttpServer.create(new InetSocketAddress(listenHost, listenPort), 0);
			this.httpServer.createContext("/applications", new ApplicationHandler());
			this.httpServer.start();
		}

		private class ApplicationHandler extends HttpAPIHandler {

			@Override
			public void handle(HttpExchange httpExchange) throws IOException {
				try {
					httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // TODO config

					if (httpExchange.getRequestMethod().equals("POST")) {
						JsonObject req = JsonObject.readFrom(new InputStreamReader(httpExchange.getRequestBody()));

					} else if (httpExchange.getRequestMethod().equals("DELETE")) {

					} else if (httpExchange.getRequestMethod().equals("GET")) {

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
				throw new UnsupportedOperationException("Method not implemented.");
			}

		}
	}
}
