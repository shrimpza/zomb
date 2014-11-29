package net.shrimpworks.zomb.entities.user;

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
