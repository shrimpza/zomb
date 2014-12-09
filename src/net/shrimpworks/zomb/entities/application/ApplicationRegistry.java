package net.shrimpworks.zomb.entities.application;

import net.shrimpworks.zomb.entities.Registry;

/**
 * In addition to regular Registry functionality, the Application Registry
 * provides a utility method, forKey(String), which retrieves a known
 * Application instance by it's key.
 */
public interface ApplicationRegistry extends Registry<Application> {

	/**
	 * Find an application for a given API key.
	 *
	 * @param key application API key
	 * @return application
	 */
	public Application forKey(String key);
}
