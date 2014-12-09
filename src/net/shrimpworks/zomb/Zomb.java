package net.shrimpworks.zomb;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.shrimpworks.zomb.api.ClientAPIService;
import net.shrimpworks.zomb.api.ClientQueryExecutor;
import net.shrimpworks.zomb.api.Help;
import net.shrimpworks.zomb.api.HttpExecutor;
import net.shrimpworks.zomb.api.PluginManager;
import net.shrimpworks.zomb.entities.application.ApplicationRegistry;
import net.shrimpworks.zomb.entities.application.ApplicationRegistryImpl;

public class Zomb {

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.err.print("HTTP listen port expected");
			System.exit(1);
		}

		final int port = Integer.parseInt(args[0]);

		final Set<ClientQueryExecutor> executors = new HashSet<>();
		executors.add(new PluginManager.Executor());
		executors.add(new Help.Executor());
		executors.add(new HttpExecutor());

		final ApplicationRegistry appRegistry = new ApplicationRegistryImpl();

		ClientAPIService service = new ClientAPIService("0.0.0.0", port, appRegistry, executors);
		System.out.printf("Listening on port %d%n", port);
	}

}
