package net.shrimpworks.zomb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import net.shrimpworks.zomb.api.ApplicationAPIService;
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
		if (args.length < 3) {
			help();
			System.exit(1);
		}

		final int apiPort = Integer.parseInt(args[0].contains(":") ? args[0].split(":")[1] : args[0]);
		final String apiAddr = args[0].contains(":") ? args[0].split(":")[0] : "0.0.0.0";

		final int adminPort = Integer.parseInt(args[1].contains(":") ? args[1].split(":")[1] : args[1]);
		final String adminAddr = args[1].contains(":") ? args[1].split(":")[0] : "0.0.0.0";

		final Path path = Paths.get(args[2]);

		if (!Files.exists(path)) {
			System.err.printf("Application storage path does not exist: %s%n", path);
			System.exit(2);
		}

		// set up client api plugin executors
		final Set<ClientQueryExecutor> executors = new HashSet<>();
		executors.add(new PluginManager.Executor());
		executors.add(new Help.Executor());
		executors.add(new HttpExecutor());

		// load up persistent application store
		final Persistence<Application> appStore = new ApplicationPersistence(new FilesystemPersistence(path), null);
		final ApplicationRegistry appRegistry = new ApplicationRegistryImpl(appStore);

		// populate default plugins
		appRegistry.all().forEach(a -> {
			a.plugins().add(new PluginManager());
			a.plugins().add(new Help());
		});

		// start api services
		final ClientAPIService clientService = new ClientAPIService(apiAddr, apiPort, appRegistry, executors);
		final ApplicationAPIService appService = new ApplicationAPIService(adminAddr, adminPort, appRegistry);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				clientService.close();
				appService.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}));

		// no errors, ready to being accepting connections
		System.out.printf("Listening on ports %d (API) and %d (management) with %d known application(s)%n",
						  apiPort, adminPort, appRegistry.all().size());
	}

	private static void help() {
		System.out.println(
				"\nusage:\n" +
				"  zomb.jar <[bind-addr:]client-port> <[bind-addr:]app-manage-port> </app/storage/path>\n\n" +
				"<[bind-addr:]client-port>\n" +
				"  the port and optional address to bind on, for accepting HTTP connections to the client API\n\n" +
				"<[bind-addr:]app-manage-port>\n" +
				"  the port and optional address to bind on, for the application management API\n\n" +
				"</app/storage/path>\n" +
				"  filesystem path to store/load application information\n"
		);
	}

}
