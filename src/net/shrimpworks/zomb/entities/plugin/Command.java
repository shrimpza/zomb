package net.shrimpworks.zomb.entities.plugin;

import java.util.regex.Pattern;

import net.shrimpworks.zomb.entities.HasName;

/**
 * A command is an individual instruction which may be issued to a
 * {@link net.shrimpworks.zomb.entities.plugin.Plugin}.
 * <p>
 * For example, the plugin named "math" might have a command "sum", executed as
 * follows:
 * <pre>
 *  math sum 30 12
 * </pre>
 */
public interface Command extends HasName {

	/**
	 * The command name. This is expected to be the second parameter of a user
	 * query, following the {@link net.shrimpworks.zomb.entities.plugin.Plugin} name.
	 *
	 * @return command name
	 */
	@Override
	public String name();

	/**
	 * Provide help or information for this command.
	 *
	 * @return help string
	 */
	public String help();

	/**
	 * Number of arguments this command accepts.
	 *
	 * @return argument count
	 */
	public int arguments();

	/**
	 * Optional regular expression to be considered for arguments when
	 * evaluating whether this command should be invoked.
	 *
	 * @return optional argument regular expression.
	 */
	public Pattern pattern();
}
