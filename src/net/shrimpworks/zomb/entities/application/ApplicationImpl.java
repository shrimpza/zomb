package net.shrimpworks.zomb.entities.application;

import net.shrimpworks.zomb.entities.plugin.PluginRegistry;
import net.shrimpworks.zomb.entities.plugin.PluginRegistryImpl;
import net.shrimpworks.zomb.entities.user.UserRegistry;
import net.shrimpworks.zomb.entities.user.UserRegistryImpl;

public class ApplicationImpl implements Application {

	private final String name;
	private final String key;
	private final String url;
	private final String contact;
	private final PluginRegistry pluginRegistry;
	private final UserRegistry users;

	public ApplicationImpl(String name, String key, String url, String contact, PluginRegistry pluginRegistry,
						   UserRegistry users) {
		this.name = name;
		this.key = key;
		this.url = url;
		this.contact = contact;
		this.pluginRegistry = pluginRegistry;
		this.users = users;
	}

	public ApplicationImpl(String name, String key, String url, String contact) {
		this(name, key, url, contact, new PluginRegistryImpl(), new UserRegistryImpl());
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String key() {
		return key;
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public String contact() {
		return contact;
	}

	@Override
	public PluginRegistry plugins() {
		return pluginRegistry;
	}

	@Override
	public UserRegistry users() {
		return users;
	}
}
