package net.shrimpworks.zomb.api;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import net.shrimpworks.zomb.common.HttpClient;
import net.shrimpworks.zomb.entities.Query;
import net.shrimpworks.zomb.entities.Response;
import net.shrimpworks.zomb.entities.ResponseImpl;
import net.shrimpworks.zomb.entities.plugin.CommandImpl;
import net.shrimpworks.zomb.entities.plugin.CommandRegistryImpl;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.plugin.PluginImpl;

public class PluginManager extends PluginImpl {

	public PluginManager() {
		super("plugin", "Provides plugin management functionality", null, null, new CommandRegistryImpl());
		commands().add(new CommandImpl("list", "list installed plugins", 0, null));
		commands().add(new CommandImpl("add", "add a new plugin", 1, null));
		commands().add(new CommandImpl("remove", "remove an installed plugin", 1, null));
	}

	public static class Executor implements ClientQueryExecutor {

		private static final Logger logger = Logger.getLogger(Executor.class.getName());

		private final HttpClient client;

		public Executor() {
			this.client = new HttpClient(5000); // TODO config
		}

		@Override
		public boolean canExecute(Plugin plugin) {
			return plugin instanceof PluginManager;
		}

		@Override
		public Response execute(Query query) {
			switch (query.command().name()) {
				case "list":
					return list(query);
				case "add":
					return add(query);
				case "remove":
					return remove(query);
				default:
					throw new IllegalArgumentException("Unknown plugin command");
			}
		}

		private Response list(Query query) {
			StringBuilder sb = new StringBuilder();
			Iterator<Plugin> plugins = query.application().plugins().all().iterator();
			while (plugins.hasNext()) {
				sb.append(plugins.next().name());
				if (plugins.hasNext()) {
					sb.append(", ");
				}
			}

			return new ResponseImpl(query.plugin(), query.user(), query.query(), new String[]{sb.toString()}, null);
		}

		private Response add(Query query) {
			try {
				String pluginDefSource = client.get(query.args().get(0));

				JsonObject pluginDef = JsonObject.readFrom(pluginDefSource);

				if (query.application().plugins().find(pluginDef.get("plugin").asString()) != null) {
					return new ResponseImpl(query, new String[]{"Failed to add plugin: already exists"}, null);
				}

				if (pluginDef.get("commands").asArray().isEmpty()) {
					return new ResponseImpl(query, new String[]{"Failed to add plugin: no commands"}, null);
				}

				Plugin plugin = new PluginImpl(
						pluginDef.get("plugin").asString(),
						pluginDef.get("help").asString(),
						query.args().get(0),
						pluginDef.get("contact").asString(),
						new CommandRegistryImpl());

				for (JsonValue commands : pluginDef.get("commands").asArray()) {
					plugin.commands().add(new CommandImpl(
							commands.asObject().get("command").asString(),
							commands.asObject().get("help").asString(),
							commands.asObject().get("args").asInt(),
							commands.asObject().get("pattern").asString()
					));
				}

				if (query.application().plugins().add(plugin)) {
					return new ResponseImpl(query, new String[]{String.format("Success, added new plugin %s", plugin.name())}, null);
				} else {
					return new ResponseImpl(query, new String[]{"Failed to add plugin to application"}, null);
				}
			} catch (IOException e) {
				logger.log(Level.WARNING, "Failed to get plugin definition", e);

				return new ResponseImpl(query, new String[]{"Failed to get plugin: " + e.getMessage()}, null);
			}
		}

		private Response remove(Query query) {
			Plugin plugin = query.application().plugins().find(query.args().get(0));
			if (plugin != null) {
				if (plugin == query.application().plugins().remove(plugin)) {
					return new ResponseImpl(query, new String[]{"Success, removed plugin"}, null);
				} else {
					return new ResponseImpl(query, new String[]{"Failed to remove plugin from application"}, null);
				}
			} else {
				return new ResponseImpl(query, new String[]{"Failed to remove plugin, does not exist"}, null);
			}
		}
	}
}
