package net.shrimpworks.zomb.entities.application;

import net.shrimpworks.zomb.entities.RegistryImpl;

public class ApplicationRegistryImpl extends RegistryImpl<Application> implements ApplicationRegistry {

	@Override
	public Application forKey(String key) {
		for (Application app : all()) {
			if (app.key().equals(key)) {
				return app;
			}
		}
		return null;
	}

}
