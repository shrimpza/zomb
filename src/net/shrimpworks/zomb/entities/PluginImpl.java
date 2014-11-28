package net.shrimpworks.zomb.entities;

public class PluginImpl implements Plugin {

	private final String name;
	private final String help;
	private final CommandRegistry commands;
	private final String url;
	private final String contact;

	public PluginImpl(String name, String help, CommandRegistry commands, String url, String contact) {
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
	public CommandRegistry commands() {
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
