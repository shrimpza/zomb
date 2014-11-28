package net.shrimpworks.zomb.entities;

/**
 * A response reveived from a plugin.
 */
public interface Response {

	/**
	 * Plugin which produced this response.
	 *
	 * @return plugin instance
	 */
	public Plugin plugin();

	/**
	 * User who issued the original query this is a response to.
	 *
	 * @return user instance
	 */
	public User user();

	/**
	 * The original query string.
	 *
	 * @return query string
	 */
	public String query();

	/**
	 * Response string(s) provided by the plugin.
	 * <p>
	 * These may be Markdown formatted.
	 *
	 * @return response strings
	 */
	public String[] response();

	/**
	 * An optional URL to an image for this response, provided by the plugin.
	 *
	 * @return optional image URL
	 */
	public String image();
}
