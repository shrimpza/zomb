package net.shrimpworks.zomb.entities;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eclipsesource.json.JsonObject;

/**
 * Persistence implementation which stores JSON objects on the file system
 * in the path provided.
 * <p>
 * If the path does not exist, it will be created at time of instantiation.
 */
public class FilesystemPersistence implements Persistence<JsonObject> {

	private static final Logger logger = Logger.getLogger(FilesystemPersistence.class.getName());

	private final Path path;

	public FilesystemPersistence(Path path) throws IOException {
		this.path = Files.createDirectories(path);
	}

	@Override
	public boolean save(JsonObject entity) throws IOException {
		return Files.write(path.resolve(entity.get("name").asString()), entity.toString().getBytes(Charset.forName("UTF-8"))) != null;
	}

	@Override
	public boolean delete(JsonObject entity) throws IOException {
		return Files.deleteIfExists(path.resolve(entity.get("name").asString()));
	}

	@Override
	public Collection<JsonObject> all() throws IOException {
		Set<JsonObject> all = new HashSet<>();
		Files.list(path).filter(Files::isRegularFile).forEach((p) -> {
			try (Reader r = Files.newBufferedReader(p)) {
				all.add(JsonObject.readFrom(r));
			} catch (IOException e) {
				logger.log(Level.WARNING, "Could not read JSON file", e);
			}
		});
		return all;
	}
}
