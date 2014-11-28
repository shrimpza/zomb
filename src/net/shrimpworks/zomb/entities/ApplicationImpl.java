package net.shrimpworks.zomb.entities;

public class ApplicationImpl implements Application {

	private final String name;
	private final String key;
	private final String url;
	private final String contact;

	public ApplicationImpl(String name, String key, String url, String contact) {
		this.name = name;
		this.key = key;
		this.url = url;
		this.contact = contact;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String key() {
		return key;
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
