package net.shrimpworks.zomb.entities.plugin;

import net.shrimpworks.zomb.entities.Registry;
import net.shrimpworks.zomb.entities.RegistryImpl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PluginRegistryTest {

	@Test
	public void pluginRegistryTest() {
		Registry<Plugin> plugins = new RegistryImpl<>();

		Plugin pee1 = new PluginImpl("pee1", "help", "linky", "author@mail", null);
		Plugin pee2 = new PluginImpl("pee2", "help2", "link2", "author@mail", null);

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

		assertTrue(plugins.all().contains(pee2));
	}

}
