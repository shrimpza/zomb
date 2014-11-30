package net.shrimpworks.zomb.api;

import net.shrimpworks.zomb.entities.Query;
import net.shrimpworks.zomb.entities.Response;

public interface ClientQueryExecutor {

	/**
	 * Execute a prepared query.
	 *
	 * @param query query to execute
	 * @return response from the query's plugin/command
	 */
	public Response execute(Query query);
}
