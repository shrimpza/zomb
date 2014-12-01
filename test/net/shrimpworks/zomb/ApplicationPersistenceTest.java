package net.shrimpworks.zomb;

import java.util.Collection;

import net.shrimpworks.zomb.entities.application.Application;

import org.junit.Test;

import static org.junit.Assert.fail;

public class ApplicationPersistenceTest {

	@Test
	public void applicationPersistenceTest() {
		fail("todo");
	}

	public static class ApplicationPersistence implements Persistence<Application> {

		@Override
		public boolean save(Application entity) {
			throw new UnsupportedOperationException("Method not implemented.");
		}

		@Override
		public boolean delete(Application entity) {
			throw new UnsupportedOperationException("Method not implemented.");
		}

		@Override
		public Collection<Application> all() {
			throw new UnsupportedOperationException("Method not implemented.");
		}
	}
}
