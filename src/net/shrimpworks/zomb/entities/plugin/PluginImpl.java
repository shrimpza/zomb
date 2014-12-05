package net.shrimpworks.zomb.entities.plugin;

import java.io.Serializable;

import net.shrimpworks.zomb.entities.Registry;
import net.shrimpworks.zomb.entities.RegistryImpl;

public class PluginImpl implements Plugin, Serializable {

	private static final long serialVersionUID = 1l;

	private final String name;
	private final String help;
	private final Registry<Command> commands;
	private final String url;
	private final String contact;

	public PluginImpl(String name, String help, String url, String contact) {
		this(name, help, url, contact, new RegistryImpl<>());
	}

	public PluginImpl(String name, String help, String url, String contact, Registry<Command> commands) {
		this.name = name;
		this.help = help;
		this.commands = commands;
		this.url = url;
		this.contact = contact;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String help() {
		return help;
	}

	@Override
	public Registry<Command> commands() {
		return commands;
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public String contact() {
		return contact;
	}
}
