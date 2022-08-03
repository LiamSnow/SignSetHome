package me.liamsnow.signsethome.filehandlers;

import me.liamsnow.signsethome.Constants;
import me.liamsnow.signsethome.SignSetHome;
import me.liamsnow.signsethome.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
	public static boolean isValidSavedLocation(Location location, UUID wantedPlayer, int wantedTag) {
		if (location == null || wantedPlayer == null) return false;

		BlockState blockState = location.getBlock().getState();
		if (!(blockState instanceof Sign)) return false;

		Sign sign = (Sign) blockState;
		PersistentDataContainer signPersistentData = sign.getPersistentDataContainer();

		int signTag = signPersistentData.getOrDefault(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_TAG_KEY), PersistentDataType.INTEGER, -1);
		if (signTag != wantedTag) return false;

		String signOwnerUUIDString = signPersistentData.getOrDefault(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_UUID_KEY), PersistentDataType.STRING, null);

		return signOwnerUUIDString != null && wantedPlayer.compareTo(UUID.fromString(signOwnerUUIDString)) == 0;
	}
	public static boolean isValidSavedLocation(Location location, Player wantedPlayer, int wantedTag) {
		return isValidSavedLocation(location, wantedPlayer.getUniqueId(), wantedTag);
	}

	public static Location getHomeLocation(UUID playerUUID) {
		return readLocation(playerUUID + ".home");
	}
	public static Location getHomeLocation(Player player) {
		return getHomeLocation(player.getUniqueId());
	}
	public static void saveHomeLocation(Player player, Location location, long griefPreventionClaimID) {
		UUID playerUUID = player.getUniqueId();
		saveLocation(playerUUID + ".home", location);
		saveGriefPreventionClaimID(playerUUID, griefPreventionClaimID);
		save();
	}
	public static void removeHomeLocation(UUID playerUUID) {
		saveLocation(playerUUID + ".home", new Location(Util.getOverworld(), Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, 0f, 0f));
		saveGriefPreventionClaimID(playerUUID, -1);
		save();
	}
	public static void removeHomeLocation(Player player) {
		removeHomeLocation(player.getUniqueId());
	}
	public static boolean hasValidHomeLocation(Player player) {
		return isValidSavedLocation(getHomeLocation(player), player, Constants.TAG_SIGN_WARP_SPAWN);
	}
	public static boolean hasValidHomeLocation(UUID playerUUID) {
		return isValidSavedLocation(getHomeLocation(playerUUID), playerUUID, Constants.TAG_SIGN_WARP_SPAWN);
	}

	public static Location getWarpSignLocation(UUID playerUUID) {
		return readLocation(playerUUID + ".warp-sign");
	}
	public static Location getWarpSignLocation(Player player) {
		return getWarpSignLocation(player.getUniqueId());
	}
	public static void saveWarpSignLocation(Player player, Location location) {
		String playerUUID = player.getUniqueId().toString();
		saveLocation(playerUUID + ".warp-sign", location);
		save();
	}
	public static boolean hasValidWarpSignLocation(Player player) {
		return isValidSavedLocation(getWarpSignLocation(player), player, Constants.TAG_SIGN_WARP_HOME_CLAIMED);
	}
	public static boolean hasValidWarpSignLocation(UUID playerUUID) {
		return isValidSavedLocation(getWarpSignLocation(playerUUID), playerUUID, Constants.TAG_SIGN_WARP_HOME_CLAIMED);
	}

	public static long getGriefPreventionClaimID(UUID playerUUID) {
		return data.getLong(playerUUID + ".griefPreventionClaimID", -1);
	}
	public static void saveGriefPreventionClaimID(UUID playerUUID, long griefPreventionClaimID) {
		data.set(playerUUID + ".griefPreventionClaimID", griefPreventionClaimID);
	}

	public static List<UUID> getAllPlayersWithHomesInClaim(long griefPreventionClaimID) {
		Set<String> allPlayerUUIDsStrings = data.getKeys(false);
		List<UUID> inClaimPlayerUUIDs = new ArrayList<UUID>();

		//Loop Through Every Player
		for (String playerUUIDString : allPlayerUUIDsStrings) {
			UUID playerUUID = UUID.fromString(playerUUIDString);
			//Check if player's home is in the claim they are asking about
			if (getGriefPreventionClaimID(playerUUID) == griefPreventionClaimID) {
				inClaimPlayerUUIDs.add(playerUUID);
			}
		}

		return inClaimPlayerUUIDs;
	}

	public static long getLastWarpHomeTimestamp(UUID playerUUID) {
		return data.getLong(playerUUID + ".lastWarpHomeTimestamp", -1);
	}

	public static void saveLastWarpHomeTimestamp(UUID playerUUID) {
		data.set(playerUUID + ".lastWarpHomeTimestamp", Util.getTimestampSeconds());
	}
}
