package net.shrimpworks.zomb.entities.user;

import net.shrimpworks.zomb.entities.HasName;

/**
 * A user.
 */
public interface User extends HasName {

	/**
	 * Name of the user.
	 *
	 * @return user name
	 */
	@Override
	public String name();
}
