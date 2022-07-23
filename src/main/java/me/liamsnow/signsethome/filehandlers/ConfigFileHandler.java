package me.liamsnow.signsethome.filehandlers;

import me.liamsnow.signsethome.SignSetHome;
import me.liamsnow.signsethome.Util;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static me.liamsnow.signsethome.Constants.CONFIG_FILE_NAME;
import static me.liamsnow.signsethome.Constants.FORCE_OVERWRITE_CONFIG_FILE;

public class ConfigFileHandler {

	private static File file;
	private static FileConfiguration config;
	private static SignSetHome instance;

	public static void init() {
		instance = SignSetHome.instance;
		load();
	}

	public static void load() {
		file = new File(instance.getDataFolder(), CONFIG_FILE_NAME);
		boolean fileExists = file.exists();

		//Make Folder for Config File
		if (!fileExists) {
			file.getParentFile().mkdirs();
		}

		//Create Config File (& Overwrite if Asked)
		if (FORCE_OVERWRITE_CONFIG_FILE || !fileExists) {
			instance.saveResource(CONFIG_FILE_NAME, FORCE_OVERWRITE_CONFIG_FILE);
		}

		config = YamlConfiguration.loadConfiguration(file);
	}

	public static void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			SignSetHome.instance.getLogger().severe(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private static Location readLocation(String key) {
		double x = config.getDouble(key + ".x");
		double y = config.getDouble(key + ".y");
		double z = config.getDouble(key + ".z");
		float yaw = (float) config.getDouble(key + ".yaw");
		float pitch = (float) config.getDouble(key + ".pitch");
		return new Location(Util.getOverworld(), x, y, z, yaw, pitch);
	}
	private static void saveLocation(String key, Location location) {
		config.set(key + ".x", location.getX());
		config.set(key + ".y", location.getY());
		config.set(key + ".z", location.getZ());
		config.set(key + ".yaw", location.getYaw());
		config.set(key + ".pitch", location.getPitch());
	}

	public static Location getSpawnLocation() {
		return readLocation("spawn-location");
	}
	public static void setSpawnLocation(Location location) {
		saveLocation("spawn-location", location);
	}

	public static Location getWarpLobbyLocation() {
		return readLocation("warp-lobby-location");
	}
	public static void setWarpLobbyLocation(Location location) {
		saveLocation("warp-lobby-location", location);
	}


}
