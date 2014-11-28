package net.shrimpworks.zomb.entities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PluginRegistryTest {

	@Test
	public void pluginRegistryTest() {
		PluginRegistry plugins = new PluginRegistryImpl();

		Plugin pee1 = new PluginImpl("pee1", "help", null, "linky", "author@mail");
		Plugin pee2 = new PluginImpl("pee2", "help2", null, "link2", "author@mail");

		assertTrue(plugins.all().isEmpty());
		assertNull(plugins.find("none"));

		assertTrue(plugins.add(pee1));
		assertEquals(pee1, plugins.find("pee1"));
		assertEquals(pee1.help(), plugins.find("pee1").help());

		assertTrue(plugins.add(pee2));
		assertEquals(pee2, plugins.find("pee2"));
		assertNotEquals(plugins.find("pee1"), plugins.find("pee2"));

		assertEquals(pee1, plugins.remove(pee1));

		assertNull(plugins.find("pee1"));
		assertEquals(pee2, plugins.find("pee2"));
	}

}
