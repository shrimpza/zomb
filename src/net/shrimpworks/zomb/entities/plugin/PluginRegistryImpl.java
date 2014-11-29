package net.shrimpworks.zomb.entities.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PluginRegistryImpl implements PluginRegistry {

	private final List<Plugin> plugins;

	public PluginRegistryImpl() {
		this.plugins = new ArrayList<>();
	}

	@Override
	public boolean add(Plugin entity) {
		return plugins.add(entity);
	}

	@Override
	public Plugin remove(Plugin entity) {
		return plugins.remove(entity) ? entity : null;
	}

	@Override
	public Plugin find(String name) {
		for (Plugin plugin : plugins) {
			if (plugin.name().equals(name)) {
				return plugin;
			}
		}
		return null;
	}

	@Override
	public List<Plugin> all() {
		return Collections.unmodifiableList(plugins);
	}
}
