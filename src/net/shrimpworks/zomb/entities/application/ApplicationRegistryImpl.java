package net.shrimpworks.zomb.entities.application;

import java.io.IOException;

import net.shrimpworks.zomb.entities.Persistence;
import net.shrimpworks.zomb.entities.PersistentRegistry;

public class ApplicationRegistryImpl extends PersistentRegistry<Application> implements ApplicationRegistry {

	public ApplicationRegistryImpl(Persistence<Application> persistence) throws IOException {
		super(persistence);
	}

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
