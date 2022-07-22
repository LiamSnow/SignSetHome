package me.liamsnow.signsethome;

import org.bukkit.Location;
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

	public static Location getSetHome(Player player) {
		List<Integer> locList = data.getIntegerList(player.getUniqueId().toString());
		if (locList.size() < 4) return null;

		return new Location(Util.getOverworld(), locList.get(0), locList.get(1), locList.get(2), locList.get(3), 0f);
	}

	public static void saveSetHome(Player player, Location location) {
		List<Integer> locList = Arrays.asList(location.getBlockX(), location.getBlockY(), location.getBlockZ(), Math.round(location.getYaw()));
		data.set(player.getUniqueId().toString(), locList);

		try {
			//FIXME ?
			data.save(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
