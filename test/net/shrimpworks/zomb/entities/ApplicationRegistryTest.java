package net.shrimpworks.zomb.entities;

import org.junit.Assert;
import org.junit.Test;

public class ApplicationRegistryTest {

	@Test
	public void appRegistryTest() {
		ApplicationRegistry reg = new ApplicationRegistryImpl();

		Application app1 = new ApplicationImpl("app1", "abc", "url", "guy");
		Application app2 = new ApplicationImpl("app2", "123", "earl", "person@mail");

		Assert.assertTrue(reg.add(app1));
		Assert.assertEquals(app1, reg.find("app1"));
		Assert.assertEquals(app1, reg.forKey("abc"));
		Assert.assertEquals(app1, reg.remove(app1));
		Assert.assertNull(reg.remove(app1));

		Assert.assertTrue(reg.all().isEmpty());

		Assert.assertTrue(reg.add(app1));
		Assert.assertTrue(reg.add(app2));

		Assert.assertTrue(reg.all().contains(app1));
		Assert.assertTrue(reg.all().contains(app2));

		Assert.assertEquals(app2, reg.find("app2"));
		Assert.assertEquals(app2, reg.forKey("123"));
	}
}
