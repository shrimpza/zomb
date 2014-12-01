package net.shrimpworks.zomb.api;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import net.jadler.Jadler;
import net.shrimpworks.zomb.common.HttpClient;
import net.shrimpworks.zomb.entities.application.ApplicationImpl;
import net.shrimpworks.zomb.entities.application.ApplicationRegistry;
import net.shrimpworks.zomb.entities.application.ApplicationRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.CommandImpl;
import net.shrimpworks.zomb.entities.plugin.CommandRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.PluginImpl;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

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
		executors.add(new PluginManager.Executor());
		executors.add(new Help.Executor());
		executors.add(new HttpExecutor());

		this.service = new ClientAPIServiceImpl(host, port, appRegistry, executors);

		this.client = new HttpClient(1000);

		this.apiUrl = String.format("http://localhost:%d", port);

		Jadler.initJadlerListeningOn(jadlerPort);
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


		/*
		 * get help for plugin's list command
		 */
		String pluginCommandHelp = client.post(apiUrl,
				new JsonObject()
						.add("key", "ckey")
						.add("user", "jane")
						.add("query", "help show plugin list")
						.toString());

		json = JsonObject.readFrom(pluginCommandHelp);

		assertEquals("jane", json.get("user").asString());
		assertEquals("help", json.get("plugin").asString());
		assertTrue(json.get("response").asArray().get(0).asString().equals("list installed plugins"));
	}

	@Test
	public void remotePluginTest() throws IOException {

		Jadler.onRequest()
				.havingPathEqualTo("/hello")
				.havingMethodEqualTo("POST")
				.havingBody(new BaseMatcher<String>() {
					@Override
					public boolean matches(Object o) {
						JsonObject json = JsonObject.readFrom(o.toString());
						return json.get("command").asString().equals("hello")
								&& json.get("application").isString()
								&& json.get("user").asString().equals("jane")
								&& json.get("args").asArray().isEmpty();
					}

					@Override
					public void describeTo(Description description) {
						// wat? :/
					}
				})
				.respond()
				.withContentType("application/json")
				.withStatus(200)
				.withBody(
						new JsonObject()
								.add("response", new JsonArray().add("hello world"))
								.add("image", "")
								.toString()
				);

		appRegistry.find("client").plugins().add(new PluginImpl("hello", null, new CommandRegistryImpl(),
				String.format("http://localhost:%d/hello", jadlerPort), null));
		appRegistry.find("client").plugins().find("hello").commands().add(new CommandImpl("hello", "say hello world", 0, null));

		String addPlugin = client.post(apiUrl,
				new JsonObject()
						.add("key", "ckey")
						.add("user", "jane")
						.add("query", "hello hello")
						.toString()
		);

		JsonObject json = JsonObject.readFrom(addPlugin);

		assertEquals("jane", json.get("user").asString());
		assertEquals("hello", json.get("plugin").asString());
		assertTrue(json.get("response").asArray().get(0).asString().equals("hello world"));
	}

}
