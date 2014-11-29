package net.shrimpworks.zomb.entities.application;

import net.shrimpworks.zomb.entities.Registry;

public interface ApplicationRegistry extends Registry<Application> {

	/**
	 * Find an application for a given API key.
	 *
	 * @param key application API key
	 * @return application
	 */
	public Application forKey(String key);
}
