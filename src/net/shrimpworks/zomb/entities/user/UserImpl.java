package net.shrimpworks.zomb.entities.user;

import java.io.Serializable;

public class UserImpl implements User, Serializable {

	private static final long serialVersionUID = 1l;

	private final String name;

	public UserImpl(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}
}
