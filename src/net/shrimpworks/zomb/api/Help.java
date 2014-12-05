package net.shrimpworks.zomb.api;

import java.util.Iterator;
import java.util.List;

import net.shrimpworks.zomb.entities.Query;
import net.shrimpworks.zomb.entities.QueryImpl;
import net.shrimpworks.zomb.entities.Response;
import net.shrimpworks.zomb.entities.ResponseImpl;
import net.shrimpworks.zomb.entities.plugin.Command;
import net.shrimpworks.zomb.entities.plugin.CommandImpl;
import net.shrimpworks.zomb.entities.plugin.Plugin;
import net.shrimpworks.zomb.entities.plugin.PluginImpl;

public class Help extends PluginImpl {

	public Help() {
		super("help", "Provides plugin and command help functions", null, null);
		commands().add(new CommandImpl("show", "shows help for a plugin or plugin command", 0, "[A-Za-z]+|[A-Za-z]+ [A-Za-z]+"));
		commands().add(new CommandImpl("list", "list commands available in a plugin", 1, null));
	}

	public static class Executor implements ClientQueryExecutor {

		@Override
		public boolean canExecute(Plugin plugin) {
			return plugin instanceof Help;
		}

		@Override
		public Response execute(Query query) {
			switch (query.command().name()) {
				case "show":
					return show(query);
				case "list":
					return list(query);
				default:
					throw new IllegalArgumentException("Unknown plugin command");
			}
		}

		private Response show(Query query) {
			List<String> args = QueryImpl.parseQuery(query.args().get(0));

			Plugin plugin = query.application().plugins().find(args.get(0));

			String help;
			if (args.size() > 1) {
				help = plugin.commands().find(args.get(1)).help();
			} else {
				help = plugin.help();
			}

			return new ResponseImpl(query, new String[]{help}, null);
		}

		private Response list(Query query) {
			Plugin plugin = query.application().plugins().find(query.args().get(0));

			StringBuilder sb = new StringBuilder();
			Iterator<Command> commands = plugin.commands().all().iterator();
			while (commands.hasNext()) {
				sb.append(commands.next().name());
				if (commands.hasNext()) {
					sb.append(", ");
				}
			}

			return new ResponseImpl(query, new String[]{sb.toString()}, null);
		}
	}
}
