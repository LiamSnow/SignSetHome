package me.liamsnow.signsethome;

import org.bukkit.Material;

public class Constants {

//	public static long LOOP_DELAY = 40l; //2 seconds
//	public static long LOOP_PERIOD = 20l;// * 60l * 5l; //5 minutes

	public static String CONFIG_FILE_NAME = "config.yml";
	public static boolean FORCE_OVERWRITE_CONFIG_FILE = false; //DISABLE FOR PRODUCTION

	public static String DATA_FILE_NAME = "data.yml";

	public static Material HOME_SIGN_MATERIAL = Material.OAK_SIGN;
	public static Material REPLACE_OLD_HOME_MATERIAL = Material.AIR;

	public static String SIGN_WARP_SPAWN_META_KEY = "signsethome-warp-spawn";
	public static String SIGN_WARP_HOME_META_KEY = "signsethome-warp-home";

}
