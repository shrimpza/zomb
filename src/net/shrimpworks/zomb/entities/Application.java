package net.shrimpworks.zomb.entities;

/**
 * An Application represents an external producer of query messages.
 * <p>
 * Applications are expected to be defined and authenticated using unique API
 * keys.
 */
public interface Application extends HasName {

	public static Application newInstance(final String name, final String key, final String url, final String contact) {
		return new Application() {

			@Override
			public String name() {
				return name;
			}

			@Override
			public String key() {
				return key;
			}

			@Override
			public String url() {
				return url;
			}

			@Override
			public String contact() {
				return contact;
			}
		};
	}

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
}
