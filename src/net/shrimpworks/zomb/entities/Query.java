package net.shrimpworks.zomb.entities;

import java.util.List;

/**
 * Represents a query to be processed by a plugin.
 * <p>
 * Queries are entered by a user in a simple format as follows:
 * <pre>
 *   command arg1 arg2
 * </pre>
 * A Query implementation is expected to parse the command and arguments.
 */
public interface Query {

	/**
	 * Application which is the source of this message.
	 *
	 * @return application instance
	 */
	public Application application();

	/**
	 * User who issued this query.
	 *
	 * @return user instance
	 */
	public User user();

	/**
	 * Plugin this query is to be processed by.
	 *
	 * @return plugin instance
	 */
	public Plugin plugin();

	/**
	 * Parsed single-word command.
	 *
	 * @return command
	 */
	public String command();

	/**
	 * Parsed list of command arguments.
	 *
	 * @return parameters
	 */
	public List<String> args();

	/**
	 * The raw command, as requested by the user.
	 *
	 * @return raw command
	 */
	public String rawCommand();
}
