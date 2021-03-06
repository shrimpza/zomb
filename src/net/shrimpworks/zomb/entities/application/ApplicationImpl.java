package net.shrimpworks.zomb.entities.application;

import net.shrimpworks.zomb.entities.Registry;
import net.shrimpworks.zomb.entities.RegistryImpl;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.user.User;

public class ApplicationImpl implements Application {

	private final String name;
	private final String key;
	private final String url;
	private final String contact;
	private final Registry<Plugin> pluginRegistry;
	private final Registry<User> users;

	public ApplicationImpl(String name, String key, String url, String contact, Registry<Plugin> pluginRegistry,
						   Registry<User> users) {
		this.name = name;
		this.key = key;
		this.url = url;
		this.contact = contact;
		this.pluginRegistry = pluginRegistry;
		this.users = users;
	}

	public ApplicationImpl(String name, String key, String url, String contact) {
		this(name, key, url, contact, new RegistryImpl<>(), new RegistryImpl<>());
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
	public Registry<Plugin> plugins() {
		return pluginRegistry;
	}

	@Override
	public Registry<User> users() {
		return users;
	}
}
