package me.liamsnow.signsethome;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

	private static File customConfigFile;
	private static FileConfiguration config;

	public static void loadConfig() {
		customConfigFile = new File(getDataFolder(), "custom.yml");
		if (!customConfigFile.exists()) {
			customConfigFile.getParentFile().mkdirs();
			saveResource("custom.yml", false);
		}

		customConfig = new YamlConfiguration();
		try {
			customConfig.load(customConfigFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
        /* User Edit:
            Instead of the above Try/Catch, you can also use
            YamlConfiguration.loadConfiguration(customConfigFile)
        */
	}

	public static Location getSpawnLocation() {
		double x = config.getDouble("spawn-location.x");
		double y = config.getDouble("spawn-location.y");
		double z = config.getDouble("spawn-location.z");
		float yaw = (float) config.getDouble("spawn-location.yaw");
		float pitch = (float) config.getDouble("spawn-location.pitch");

		return new Location();
	}

}
