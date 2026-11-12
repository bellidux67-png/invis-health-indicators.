package dev.estia.invishealth;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

/** Settings saved to .minecraft/config/invishealth.json so they survive restarts. */
public final class InvisHealthConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("invishealth.json");

	public static InvisHealthConfig INSTANCE = new InvisHealthConfig();

	/** Master switch for all indicators. */
	public boolean enabled = true;
	/** Hearts above mobs (players are controlled separately). */
	public boolean showMobs = true;
	/** Hearts above players who are invisible from a potion. */
	public boolean showInvisible = true;
	/** Extra height above the default spot, in blocks (negative moves the hearts down). */
	public double heightOffset = 0.0;

	public static final double MIN_HEIGHT = -1.5;
	public static final double MAX_HEIGHT = 2.0;

	public static void load() {
		if (!Files.exists(FILE)) {
			save();
			return;
		}
		try (Reader reader = Files.newBufferedReader(FILE)) {
			InvisHealthConfig loaded = GSON.fromJson(reader, InvisHealthConfig.class);
			if (loaded != null) INSTANCE = loaded;
		} catch (Exception e) {
			InvisHealthClient.LOGGER.warn("Couldn't read {}, using defaults", FILE, e);
		}
	}

	public static void save() {
		try {
			Files.createDirectories(FILE.getParent());
			try (Writer writer = Files.newBufferedWriter(FILE)) {
				GSON.toJson(INSTANCE, writer);
			}
		} catch (Exception e) {
			InvisHealthClient.LOGGER.warn("Couldn't save {}", FILE, e);
		}
	}
}
