package me.liamsnow.signsethome;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static me.liamsnow.signsethome.Constants.CONFIG_FILE_NAME;
import static me.liamsnow.signsethome.Constants.FORCE_OVERWRITE_CONFIG_FILE;

public class ConfigHandler {

	private static File configFile;
	private static FileConfiguration config;
	private static SignSetHome instance;

	public static void init() {
		instance = SignSetHome.instance;
		loadConfig();
	}

	public static void loadConfig() {
		configFile = new File(instance.getDataFolder(), CONFIG_FILE_NAME);
		boolean configFileExists = configFile.exists();

		//Make Folder for Config File
		if (!configFileExists) {
			configFile.getParentFile().mkdirs();
		}

		//Create Config File (& Overwrite if Asked)
		if (FORCE_OVERWRITE_CONFIG_FILE || !configFileExists) {
			instance.saveResource(CONFIG_FILE_NAME, FORCE_OVERWRITE_CONFIG_FILE);
		}

		config = YamlConfiguration.loadConfiguration(configFile);
	}

	public static Location getSpawnLocation() {
		double x = config.getDouble("spawn-location.x");
		double y = config.getDouble("spawn-location.y");
		double z = config.getDouble("spawn-location.z");
		float yaw = (float) config.getDouble("spawn-location.yaw");
		float pitch = (float) config.getDouble("spawn-location.pitch");
		return new Location(Util.getOverworld(), x, y, z, yaw, pitch);
	}
}
