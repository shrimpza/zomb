package net.shrimpworks.zomb.api;

import net.shrimpworks.zomb.entities.Query;
import net.shrimpworks.zomb.entities.Response;
import net.shrimpworks.zomb.entities.plugin.Plugin;

/**
 * A Client Query Executor implementation is expected to process a
 * query passed to it via the API, and return a response, which the
 * API will in turn return to the client.
 */
public interface ClientQueryExecutor {

	/**
	 * Test whether this executor may execute queries for a specific plugin.
	 *
	 * @param plugin plugin to check
	 * @return true if this executor can execute queries for this plugin
	 */
	public boolean canExecute(Plugin plugin);

	/**
	 * Execute a prepared query.
	 *
	 * @param query query to execute
	 * @return response from the query's plugin/command
	 */
	public Response execute(Query query);
}
