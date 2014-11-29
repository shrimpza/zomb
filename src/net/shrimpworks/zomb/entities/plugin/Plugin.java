package net.shrimpworks.zomb.entities.plugin;

import net.shrimpworks.zomb.entities.CommandRegistry;
import net.shrimpworks.zomb.entities.HasName;

/**
 * Plugins implement the actual functionality of the system.
 */
public interface Plugin extends HasName {

	/**
	 * Plugin name. This name is expected to be the first argument of a user
	 * query, so this plugin may be identified as the target of the query.
	 *
	 * @return plugin name
	 */
	@Override
	public String name();

	/**
	 * Provides help or information for this plugin.
	 *
	 * @return help string
	 */
	public String help();

	/**
	 * Commands supported by this plugin. Used for parsing and validating user
	 * queries.
	 *
	 * @return registry of supported commands
	 */
	public CommandRegistry commands();

	/**
	 * URL to be accessed to process commands issued to this plugin.
	 *
	 * @return plugin API URL
	 */
	public String url();

	/**
	 * Contact for the plugin author.
	 *
	 * @return author contact
	 */
	public String contact();
}
