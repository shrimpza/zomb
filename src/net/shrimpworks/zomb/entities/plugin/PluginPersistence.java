package net.shrimpworks.zomb.entities.plugin;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import net.shrimpworks.zomb.entities.Persistence;
import net.shrimpworks.zomb.entities.Registry;
import net.shrimpworks.zomb.entities.RegistryImpl;

public class PluginPersistence implements Persistence<Plugin> {

	private final Persistence<JsonObject> persistence;

	public PluginPersistence(Persistence<JsonObject> persistence) {
		this.persistence = persistence;
	}

	@Override
	public boolean save(Plugin entity) throws IOException {
		JsonObject json = new JsonObject()
				.add("name", entity.name())
				.add("help", entity.help())
				.add("url", entity.url())
				.add("contact", entity.contact())
				.add("commands", commands(entity.commands()));
		return persistence.save(json);
	}

	@Override
	public boolean delete(Plugin entity) throws IOException {
		return persistence.delete(new JsonObject().add("name", entity.name()));
	}

	@Override
	public Collection<Plugin> all() throws IOException {
		Collection<JsonObject> json = persistence.all();

		Set<Plugin> all = json.stream().map(j -> new PluginImpl(
				j.get("name").asString(),
				j.get("help").asString(),
				j.get("url").asString(),
				j.get("contact").asString(),
				commands(j.get("commands").asArray())
		)).collect(Collectors.toSet());

		return Collections.unmodifiableCollection(all);
	}

	private JsonArray commands(Registry<Command> commands) {
		JsonArray jsonArray = new JsonArray();
		commands.all().forEach(c -> jsonArray.add(
				new JsonObject()
						.add("name", c.name())
						.add("help", c.help())
						.add("args", c.arguments())
						.add("pattern", c.pattern() == null ? null : c.pattern().pattern())
		));
		return jsonArray;
	}

	private Registry<Command> commands(JsonArray jsonArray) {
		Registry<Command> commands = new RegistryImpl<>();
		jsonArray.forEach(j -> commands.add(new CommandImpl(
						j.asObject().get("name").asString(),
						j.asObject().get("help").asString(),
						j.asObject().get("args").asInt(),
						j.asObject().get("pattern").isNull() ? null : j.asObject().get("pattern").asString()
				))
		);
		return commands;
	}
}
