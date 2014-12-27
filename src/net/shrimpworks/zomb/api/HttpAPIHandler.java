package net.shrimpworks.zomb.api;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class HttpAPIHandler implements HttpHandler {

	protected void respond(HttpExchange exchange, int status) throws IOException {
		respond(exchange, status, null);
	}

	protected void respond(HttpExchange exchange, int status, String body) throws IOException {
		exchange.sendResponseHeaders(status, body == null || body.isEmpty() ? -1 : body.length());
		if (body != null && !body.isEmpty()) exchange.getResponseBody().write(body.getBytes());
	}
}
