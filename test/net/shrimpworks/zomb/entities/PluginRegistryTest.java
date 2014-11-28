package net.shrimpworks.zomb.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	public static class PluginRegistryImpl implements PluginRegistry {

		private final List<Plugin> plugins;

		public PluginRegistryImpl() {
			this.plugins = new ArrayList<>();
		}

		@Override
		public boolean add(Plugin entity) {
			return plugins.add(entity);
		}

		@Override
		public Plugin remove(Plugin entity) {
			return plugins.remove(entity) ? entity : null;
		}

		@Override
		public Plugin find(String name) {
			for (Plugin plugin : plugins) {
				if (plugin.name().equals(name)) {
					return plugin;
				}
			}
			return null;
		}

		@Override
		public List<Plugin> all() {
			return Collections.unmodifiableList(plugins);
		}
	}

	public static class PluginImpl implements Plugin {

		private final String name;
		private final String help;
		private final CommandRegistry commands;
		private final String url;
		private final String contact;

		public PluginImpl(String name, String help, CommandRegistry commands, String url, String contact) {
			this.name = name;
			this.help = help;
			this.commands = commands;
			this.url = url;
			this.contact = contact;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public String help() {
			return help;
		}

		@Override
		public CommandRegistry commands() {
			return commands;
		}

		@Override
		public String url() {
			return url;
		}

		@Override
		public String contact() {
			return contact;
		}
	}
}
