package net.shrimpworks.zomb.entities;

/**
 *
 *
 */
public interface User extends HasName {

	/**
	 * Name of the user.
	 *
	 * @return user name
	 */
	@Override
	public String name();

	/**
	 * Application from whence this user originates.
	 *
	 * @return application instance
	 */
	public Application application();
}
