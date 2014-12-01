package net.shrimpworks.zomb.entities;

import net.shrimpworks.zomb.entities.Response;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.user.User;

public class ResponseImpl implements Response {

	private final Plugin plugin;
	private final User user;
	private final String query;
	private final String[] response;
	private final String image;

	public ResponseImpl(Plugin plugin, User user, String query, String[] response, String image) {
		this.plugin = plugin;
		this.user = user;
		this.query = query;
		this.response = response;
		this.image = image;
	}

	@Override
	public Plugin plugin() {
		return plugin;
	}

	@Override
	public User user() {
		return user;
	}

	@Override
	public String query() {
		return query;
	}

	@Override
	public String[] response() {
		return response.clone();
	}

	@Override
	public String image() {
		return image;
	}
}
