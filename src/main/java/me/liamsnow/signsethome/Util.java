package me.liamsnow.signsethome;

import org.bukkit.World;

public class Util {

	public static World getOverworld() {
		return SignSetHome.instance.getServer().getWorlds().get(0);
	}

}
