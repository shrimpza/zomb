package net.shrimpworks.zomb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import net.shrimpworks.zomb.api.ClientAPIService;
import net.shrimpworks.zomb.api.ClientQueryExecutor;
import net.shrimpworks.zomb.api.Help;
import net.shrimpworks.zomb.api.HttpExecutor;
import net.shrimpworks.zomb.api.PluginManager;
import net.shrimpworks.zomb.entities.FilesystemPersistence;
import net.shrimpworks.zomb.entities.Persistence;
import net.shrimpworks.zomb.entities.application.Application;
import net.shrimpworks.zomb.entities.application.ApplicationPersistence;
import net.shrimpworks.zomb.entities.application.ApplicationRegistry;
import net.shrimpworks.zomb.entities.application.ApplicationRegistryImpl;

public class Zomb {

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.err.println("HTTP listen port and application storage path expected");
			System.exit(1);
		}

		final int port = Integer.parseInt(args[0]);
		final Path path = Paths.get(args[1]);

		if (!Files.exists(path)) {
			System.err.println("Application storage path does not exist");
			System.exit(1);
		}

		final Set<ClientQueryExecutor> executors = new HashSet<>();
		executors.add(new PluginManager.Executor());
		executors.add(new Help.Executor());
		executors.add(new HttpExecutor());

		Persistence<Application> appStore = new ApplicationPersistence(new FilesystemPersistence(path), null);
		ApplicationRegistry appRegistry = new ApplicationRegistryImpl(appStore);

		ClientAPIService service = new ClientAPIService("0.0.0.0", port, appRegistry, executors);
		System.out.printf("Listening on port %d with %d known application(s)%n", port, appRegistry.all().size());
	}

}
