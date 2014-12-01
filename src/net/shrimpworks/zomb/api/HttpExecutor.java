package net.shrimpworks.zomb.api;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import net.shrimpworks.zomb.common.HttpClient;
import net.shrimpworks.zomb.entities.Query;
import net.shrimpworks.zomb.entities.Response;
import net.shrimpworks.zomb.entities.ResponseImpl;
import net.shrimpworks.zomb.entities.plugin.Plugin;

public class HttpExecutor implements ClientQueryExecutor {

	private static final Logger logger = Logger.getLogger(HttpExecutor.class.getName());

	private final HttpClient client;

	public HttpExecutor() {
		this.client = new HttpClient(5000); // TODO config
	}

	@Override
	public boolean canExecute(Plugin plugin) {
		return plugin.url() != null && plugin.url().matches("http://.+");
	}

	@Override
	public Response execute(Query query) {
		try {
			JsonArray args = new JsonArray();
			query.args().forEach(args::add);

			String res = client.post(query.plugin().url(),
					new JsonObject()
							.add("application", query.application().key()) // TODO salt and rehash
							.add("user", query.user().name())
							.add("command", query.command().name())
							.add("args", args)
							.toString()
			);

			JsonObject json = JsonObject.readFrom(res);

			String[] response = new String[json.get("response").asArray().size()];
			for (int i = 0; i < json.get("response").asArray().size(); i++) {
				response[i] = json.get("response").asArray().get(0).asString();
			}

			return new ResponseImpl(query, response, json.get("image").asString());
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to execute remote plugin", e);

			return new ResponseImpl(query, new String[]{"Failed to execute plugin"}, null);
		}
	}
}
