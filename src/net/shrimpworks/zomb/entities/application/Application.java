package net.shrimpworks.zomb.entities.application;

import net.shrimpworks.zomb.entities.HasName;
import net.shrimpworks.zomb.entities.plugin.PluginRegistry;
import net.shrimpworks.zomb.entities.user.UserRegistry;

/**
 * An Application represents an external producer of query messages.
 * <p>
 * Applications are expected to be defined and authenticated using unique API
 * keys.
 */
public interface Application extends HasName {

	/**
	 * Application name.
	 *
	 * @return application name
	 */
	@Override
	public String name();

	/**
	 * Unique authentication key used to grant this application access.
	 *
	 * @return API key
	 */
	public String key();

	/**
	 * URL to this application's home page, or a page where further information
	 * about the application may be found.
	 *
	 * @return application homepage/link
	 */
	public String url();

	/**
	 * Contact for the application author.
	 *
	 * @return author contact
	 */
	public String contact();

	/**
	 * All active plugins associated with this application.
	 *
	 * @return plugin registry
	 */
	public PluginRegistry plugins();

	/**
	 * All users who have used this application.
	 *
	 * @return user registry
	 */
	public UserRegistry users();
}
