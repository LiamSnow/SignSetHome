package me.liamsnow.signsethome;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import java.util.UUID;

public class Util {

	public static World getOverworld() {
		return SignSetHome.instance.getServer().getWorlds().get(0);
	}

	public static String getPlayerUsernameFromUUID(UUID playerUUID) {
		return Bukkit.getOfflinePlayer(playerUUID).getName();
	}

	/* Credit to K3ttle (spigotmc.org) */
	public static final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	public static final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
	public static BlockFace yawToFace(float yaw) {
		return yawToFace(yaw, true);
	}
	public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
		if (useSubCardinalDirections) {
			return radial[Math.round(yaw / 45f) & 0x7];
		} else {
			return axis[Math.round(yaw / 90f) & 0x3];
		}
	}
	/* End Credit */

}
