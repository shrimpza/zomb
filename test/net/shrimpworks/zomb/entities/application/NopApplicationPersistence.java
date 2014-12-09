package net.shrimpworks.zomb.entities.application;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import net.shrimpworks.zomb.entities.Persistence;

public class NopApplicationPersistence implements Persistence<Application> {

	@Override
	public boolean save(Application entity) throws IOException {
		return true;
	}

	@Override
	public boolean delete(Application entity) throws IOException {
		return true;
	}

	@Override
	public Collection<Application> all() throws IOException {
		return Collections.emptyList();
	}
}
