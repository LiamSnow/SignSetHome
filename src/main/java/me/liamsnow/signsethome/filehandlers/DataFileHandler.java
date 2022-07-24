package me.liamsnow.signsethome.filehandlers;

import me.liamsnow.signsethome.Constants;
import me.liamsnow.signsethome.SignSetHome;
import me.liamsnow.signsethome.Util;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static me.liamsnow.signsethome.Constants.DATA_FILE_NAME;

public class DataFileHandler {

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
	public static boolean isValidSavedLocation(Location location, String wantedUUID, int wantedTag) {
		if (location == null || wantedUUID == null) return false;

		BlockState blockState = location.getBlock().getState();
		if (!(blockState instanceof Sign)) return false;

		Sign sign = (Sign) blockState;
		PersistentDataContainer signPersistentData = sign.getPersistentDataContainer();
		int signTag = signPersistentData.getOrDefault(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_TAG_KEY), PersistentDataType.INTEGER, -1);
		String signUUID = signPersistentData.getOrDefault(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_UUID_KEY), PersistentDataType.STRING, null);

		return signTag == wantedTag && signUUID != null && signUUID.equals(wantedUUID);
	}
	public static boolean isValidSavedLocation(Location location, Player wantedPlayer, int wantedTag) {
		return isValidSavedLocation(location, wantedPlayer.getUniqueId().toString(), wantedTag);
	}

	public static Location getHomeLocation(String UUID) {
		return readLocation(UUID + ".home");
	}
	public static Location getHomeLocation(Player player) {
		return getHomeLocation(player.getUniqueId().toString());
	}
	public static void saveHomeLocation(Player player, Location location) {
		String playerUUID = player.getUniqueId().toString();
		saveLocation(playerUUID + ".home", location);
		data.set(playerUUID + ".username", player.getDisplayName());
		save();
	}
	public static boolean hasValidHomeLocation(Player targetPlayer) {
		return isValidSavedLocation(getHomeLocation(targetPlayer), targetPlayer, Constants.TAG_SIGN_WARP_SPAWN);
	}
	public static boolean hasValidHomeLocation(String targetUUID) {
		return isValidSavedLocation(getHomeLocation(targetUUID), targetUUID, Constants.TAG_SIGN_WARP_SPAWN);
	}

	public static Location getWarpSignLocation(String UUID) {
		return readLocation(UUID + ".warp-sign");
	}
	public static Location getWarpSignLocation(Player player) {
		return getWarpSignLocation(player.getUniqueId().toString());
	}
	public static void saveWarpSignLocation(Player player, Location location) {
		String playerUUID = player.getUniqueId().toString();
		saveLocation(playerUUID + ".warp-sign", location);
		data.set(playerUUID + ".username", player.getDisplayName());
		save();
	}
	public static boolean hasValidWarpSignLocation(Player targetPlayer) {
		return isValidSavedLocation(getWarpSignLocation(targetPlayer), targetPlayer, Constants.TAG_SIGN_WARP_HOME_CLAIMED);
	}
	public static boolean hasValidWarpSignLocation(String targetUUID) {
		return isValidSavedLocation(getWarpSignLocation(targetUUID), targetUUID, Constants.TAG_SIGN_WARP_HOME_CLAIMED);
	}

	public static String getUsername(String playerUUID) {
		return data.getString(playerUUID + ".username");
	}
}