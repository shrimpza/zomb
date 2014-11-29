package net.shrimpworks.zomb.entities;

public class UserImpl implements User {

	private final String name;

	public UserImpl(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}
}
