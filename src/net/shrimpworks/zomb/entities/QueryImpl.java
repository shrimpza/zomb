package net.shrimpworks.zomb.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.plugin.Command;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.user.User;

public class QueryImpl implements Query {

	private final Application application;
	private final User user;
	private final Plugin plugin;
	private final Command command;
	private final List<String> args;
	private final String query;

	public QueryImpl(Application application, User user, String query) {
		this.application = application;
		this.user = user;
		this.query = query;

		List<String> queryParts = queryParts(query);

		if (queryParts.size() < 2)
			throw new IllegalArgumentException("Invalid query: cannot determine plugin and command");

		this.plugin = application.plugins().find(queryParts.remove(0));
		if (this.plugin == null) throw new IllegalArgumentException("Invalid query: plugin not found");

		this.command = this.plugin.commands().find(queryParts.remove(0));
		if (this.command == null) throw new IllegalArgumentException("Invalid query: command not found");

		if (this.command.pattern() != null) {
			String queryString = query.replaceFirst(this.plugin.name() + " " + this.command.name() + " ", "");
			if (!queryString.matches(this.command.pattern().pattern()))
				throw new IllegalArgumentException("Invalid query: query does not match required format, see command help");

			this.args = Collections.singletonList(queryString);
		} else {
			if (this.command.arguments() > 0 && queryParts.size() != this.command.arguments())
				throw new IllegalArgumentException("Invalid query: incorrect argument count, see command help");

			this.args = queryParts;
		}
	}

	@Override
	public Application application() {
		return application;
	}

	@Override
	public User user() {
		return user;
	}

	@Override
	public Plugin plugin() {
		return plugin;
	}

	@Override
	public Command command() {
		return command;
	}

	@Override
	public List<String> args() {
		return Collections.unmodifiableList(args);
	}

	@Override
	public String query() {
		return query;
	}

	private List<String> queryParts(String query) {
		List<String> res = new ArrayList<>();
		Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
		Matcher regexMatcher = regex.matcher(query);

		while (regexMatcher.find()) {
			if (regexMatcher.group(1) != null) {
				// Add double-quoted string without the quotes
				res.add(regexMatcher.group(1));
			} else if (regexMatcher.group(2) != null) {
				// Add single-quoted string without the quotes
				res.add(regexMatcher.group(2));
			} else {
				// Add unquoted word
				res.add(regexMatcher.group());
			}
		}

		return res;
	}
}
