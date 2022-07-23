package me.liamsnow.signsethome;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static me.liamsnow.signsethome.Constants.DATA_FILE_NAME;

public class DataHandler {

	private static File file;
	private static FileConfiguration data;
	private static SignSetHome instance;

	public static void init() {
		instance = SignSetHome.instance;
		load();
	}

	public static void load() {
		file = new File(instance.getDataFolder(), DATA_FILE_NAME);

		//Make Folder for Config File
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			instance.saveResource(DATA_FILE_NAME, false);
		}

		data = YamlConfiguration.loadConfiguration(file);
	}

	public static void save() {
		try {
			data.save(file);
		} catch (IOException e) {
			SignSetHome.instance.getLogger().severe(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private static Location readLocation(String key) {
		double x = data.getDouble(key + ".x", Double.MAX_VALUE);
		double y = data.getDouble(key + ".y", Double.MAX_VALUE);
		double z = data.getDouble(key + ".z", Double.MAX_VALUE);
		float yaw = (float) data.getDouble(key + ".yaw", Double.MAX_VALUE);
		float pitch = (float) data.getDouble(key + ".pitch", Double.MAX_VALUE);

		if (Arrays.asList(x, y, z, yaw, pitch).contains(Double.MAX_VALUE)) return null;

		return new Location(Util.getOverworld(), x, y, z, yaw, 0);
	}
	private static void saveLocation(String key, Location location) {
		data.set(key + ".x", location.getX());
		data.set(key + ".y", location.getY());
		data.set(key + ".z", location.getZ());
		data.set(key + ".yaw", location.getYaw());
	}
	public static Location getHomeLocation(String UUID) {
		return readLocation(UUID + ".home");
	}
	public static Location getHomeLocation(Player player) {
		return getHomeLocation(player.getUniqueId().toString());
	}

	public static void saveHomeLocation(Player player, Location location) {
		saveLocation(player.getUniqueId().toString() + ".home", location);
		save();
	}

	public static Location getWarpSignLocation(String UUID) {
		return readLocation(UUID + ".warp-sign");
	}
	public static Location getWarpSignLocation(Player player) {
		return getWarpSignLocation(player.getUniqueId().toString());
	}

	public static void saveWarpSignLocation(Player player, Location location) {
		saveLocation(player.getUniqueId().toString() + ".warp-sign", location);
		save();
	}

}
