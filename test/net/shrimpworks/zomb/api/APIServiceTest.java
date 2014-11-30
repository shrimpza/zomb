package net.shrimpworks.zomb.api;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.application.ApplicationRegistry;
import net.shrimpworks.zomb.entities.application.ApplicationRegistryImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

public class APIServiceTest {

	private static final String host = "0.0.0.0";
	private static final int port = 8090;

	private ApplicationRegistry appRegistry;
	private APIService service;

	@Before
	public void setup() throws IOException {
		this.appRegistry = new ApplicationRegistryImpl();

		this.service = new APIService(host, port, appRegistry);
	}

	@After
	public void teardown() throws IOException {
		this.service.close();
	}

	@Test
	public void queryTest() {
		this.appRegistry.add(new ApplicationImpl("client", "ckey", null, null));

		fail("todo");
	}

	public static class APIService implements Closeable {

		private final HttpServer httpServer;
		private final ApplicationRegistry appRegistry;

		public APIService(String listenHost, int listenPort, ApplicationRegistry appRegistry) throws IOException {
			this.appRegistry = appRegistry;

			this.httpServer = HttpServer.create(new InetSocketAddress(listenHost, listenPort), 0);
			this.httpServer.start();
		}

		@Override
		public void close() throws IOException {
			this.httpServer.stop(0);
		}
	}
}
