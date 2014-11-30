package net.shrimpworks.zomb.api;

import java.io.Closeable;
import java.util.Set;

public interface ClientAPIService extends Closeable {

	/**
	 * Collection of query executors this service knows about.
	 *
	 * @return client query executors
	 */
	public Set<ClientQueryExecutor> executors();
}
