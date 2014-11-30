package net.shrimpworks.zomb.common;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.jadler.Jadler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class HttpClientTest {

	private static final int PORT = 8091;

	@Before
	public void setup() {
		Jadler.initJadlerListeningOn(PORT);
	}

	@After
	public void teardown() {
		Jadler.closeJadler();
	}

	@Test
	public void httpClientTest() throws IOException {
		final String post = "hello world";
		final String response = "sup";

		Jadler.onRequest()
				.havingMethodEqualTo("GET")
				.havingPathEqualTo("/test/get")
				.respond()
				.withStatus(200)
				.withBody(response);

		Jadler.onRequest()
				.havingMethodEqualTo("POST")
				.havingPathEqualTo("/test/post")
				.havingBodyEqualTo(post)
				.respond()
				.withStatus(200)
				.withBody(response);

		HttpClient client = new HttpClient(1000);

		assertEquals(response, client.get(String.format("http://localhost:%d/test/get", PORT)));

		assertEquals(response, client.post(String.format("http://localhost:%d/test/post", PORT), post));

		try {
			client.get(String.format("http://localhost:%d/test/dunno", PORT));
			fail("Expected a 404");
		} catch (FileNotFoundException expected) {
			// expected
		}
	}
}
