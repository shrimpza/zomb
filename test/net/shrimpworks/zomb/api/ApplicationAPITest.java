package net.shrimpworks.zomb.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import net.shrimpworks.zomb.common.HttpClient;
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

	private ApplicationAPIService service;
	private HttpClient client;
	private String apiUrl;

	@Before
	public void setup() throws IOException {
		ApplicationRegistry appRegistry = new ApplicationRegistryImpl(new NopApplicationPersistence());

		appRegistry.add(new ApplicationImpl("client", "ckey", null, null));
		appRegistry.find("client").plugins().add(new PluginManager());
		appRegistry.find("client").plugins().add(new Help());

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

}
