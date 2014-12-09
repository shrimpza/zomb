package net.shrimpworks.zomb.entities.application;

import java.io.IOException;
import java.nio.file.Path;

import net.shrimpworks.zomb.entities.FilesystemPersistence;
import net.shrimpworks.zomb.entities.Persistence;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.plugin.PluginPersistence;
import net.shrimpworks.zomb.entities.user.User;
import net.shrimpworks.zomb.entities.user.UserPersistence;

public class ApplicationFilesystemPersistenceFactory implements ApplicationPersistence.ApplicationPersistenceFactory {

	private final Path path;

	public ApplicationFilesystemPersistenceFactory(Path path) {
		this.path = path;
	}

	@Override
	public Persistence<Plugin> pluginPersistence(String application) throws IOException {
		return new PluginPersistence(new FilesystemPersistence(path.resolve(application + ".plugins")));
	}

	@Override
	public Persistence<User> userPersistence(String application) throws IOException {
		return new UserPersistence(new FilesystemPersistence(path.resolve(application + ".users")));
	}
}
