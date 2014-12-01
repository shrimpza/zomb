package net.shrimpworks.zomb.entities.plugin;

import java.io.Serializable;
import java.util.regex.Pattern;

public class CommandImpl implements Command, Serializable {

	private static final long serialVersionUID = 1l;

	private final String name;
	private final String help;
	private final int args;
	private final Pattern pattern;

	public CommandImpl(String name, String help, int args, String pattern) {
		this.name = name;
		this.help = help;
		this.args = args;
		this.pattern = pattern != null && !pattern.isEmpty() ? Pattern.compile(pattern) : null;
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
	public int arguments() {
		return args;
	}

	@Override
	public Pattern pattern() {
		return pattern;
	}
}
