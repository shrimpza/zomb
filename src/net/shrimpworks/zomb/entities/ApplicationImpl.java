package net.shrimpworks.zomb.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ApplicationImpl implements Application {

	private final String name;
	private final String key;
	private final String url;
	private final String contact;
	private final PluginRegistry pluginRegistry;
	private final Set<User> users;

	public ApplicationImpl(String name, String key, String url, String contact, PluginRegistry pluginRegistry,
						   Set<User> users) {
		this.name = name;
		this.key = key;
		this.url = url;
		this.contact = contact;
		this.pluginRegistry = pluginRegistry;
		this.users = users;
	}

	public ApplicationImpl(String name, String key, String url, String contact) {
		this(name, key, url, contact, new PluginRegistryImpl(), new HashSet<>());
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
	public Set<User> users() {
		return Collections.unmodifiableSet(users);
	}
}
