package me.liamsnow.signsethome;

import org.bukkit.Material;

public class Constants {

//	public static long LOOP_DELAY = 40l; //2 seconds
//	public static long LOOP_PERIOD = 20l;// * 60l * 5l; //5 minutes

	public static String CONFIG_FILE_NAME = "config.yml";
	public static boolean FORCE_OVERWRITE_CONFIG_FILE = true; //DISABLE FOR PRODUCTION

	public static String DATA_FILE_NAME = "data.yml";

	public static Material HOME_SIGN_MATERIAL = Material.OAK_SIGN;
	public static Material WARP_HOME_SIGN_MATERIAL = Material.OAK_SIGN;
	public static Material REPLACE_OLD_HOME_MATERIAL = Material.AIR;

	public static String PERSISTENT_DATA_TAG_KEY = "signsethome-tag";
	public static String PERSISTENT_DATA_UUID_KEY = "signsethome-uuid";
	public static int TAG_SIGN_WARP_SPAWN = 34;
	public static int TAG_SIGN_WARP_HOME_UNCLAIMED = 5104;
	public static int TAG_SIGN_WARP_HOME_CLAIMED = 254;

}
