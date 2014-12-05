package net.shrimpworks.zomb.entities.application;

import java.io.Serializable;

import net.shrimpworks.zomb.entities.Registry;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.plugin.PluginRegistryImpl;
import net.shrimpworks.zomb.entities.user.User;
import net.shrimpworks.zomb.entities.user.UserRegistryImpl;

public class ApplicationImpl implements Application, Serializable {

	private static final long serialVersionUID = 1l;

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
	public Registry<Plugin> plugins() {
		return pluginRegistry;
	}

	@Override
	public Registry<User> users() {
		return users;
	}
}
