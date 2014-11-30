package net.shrimpworks.zomb.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.sun.net.httpserver.HttpServer;
import net.shrimpworks.zomb.entities.Query;
import net.shrimpworks.zomb.entities.Response;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.application.ApplicationRegistry;
import net.shrimpworks.zomb.entities.application.ApplicationRegistryImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

		this.service = new ClientAPIServiceImpl(host, port, appRegistry, executors);
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

	public static class ClientAPIServiceImpl implements ClientAPIService {

		private final HttpServer httpServer;
		private final ApplicationRegistry appRegistry;
		private final Set<ClientQueryExecutor> executors;

		public ClientAPIServiceImpl(String listenHost, int listenPort, ApplicationRegistry appRegistry,
									Set<ClientQueryExecutor> executors) throws IOException {
			this.appRegistry = appRegistry;
			this.executors = executors;

			this.httpServer = HttpServer.create(new InetSocketAddress(listenHost, listenPort), 0);
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
	}

	public static class PluginManagementExecutor implements ClientQueryExecutor {

		@Override
		public Response execute(Query query) {
			throw new UnsupportedOperationException("Method not implemented.");
		}
	}

	public static class HelpExecutor implements ClientQueryExecutor {
		@Override
		public Response execute(Query query) {
			throw new UnsupportedOperationException("Method not implemented.");
		}
	}
}
