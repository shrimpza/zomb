package net.shrimpworks.zomb.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplicationRegistryImpl implements ApplicationRegistry {

	private final List<Application> applications;

	public ApplicationRegistryImpl() {
		this.applications = new ArrayList<>();
	}

	@Override
	public boolean add(Application entity) {
		return applications.add(entity);
	}

	@Override
	public Application remove(Application entity) {
		return applications.remove(entity) ? entity : null;
	}

	@Override
	public List<Application> all() {
		return Collections.unmodifiableList(applications);
	}

	@Override
	public Application find(String name) {
		for (Application app : applications) {
			if (app.name().equals(name)) {
				return app;
			}
		}
		return null;
	}

	@Override
	public Application forKey(String key) {
		for (Application app : applications) {
			if (app.key().equals(key)) {
				return app;
			}
		}
		return null;
	}

}
