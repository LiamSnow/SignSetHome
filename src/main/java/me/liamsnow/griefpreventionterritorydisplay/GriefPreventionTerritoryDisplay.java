package me.liamsnow.griefpreventionterritorydisplay;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class GriefPreventionTerritoryDisplay extends JavaPlugin {

	GriefPrevention griefPreventionPlugin;

	private final long LOOP_DELAY = 40l; //2 seconds
	private final long LOOP_PERIOD = 20l;// * 60l * 5l; //5 minutes

	@Override
	public void onEnable() {
//		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
//			getServer().broadcastMessage("Update");
//
//			Block block = getServer().getWorld("world").getBlockAt(-109, 107, 14);
//			block.setType(Material.OAK_WALL_SIGN);
//
//			Sign sign = (Sign) block.getState();
//			sign.setLine(0, "E" + Math.random());
//			sign.setLine(1, "" + Math.random());
//			sign.setLine(2, "" + Math.random());
//			sign.setLine(3, "" + Math.random());
//			sign.update();
//
//		}, 0L, 10L);

		//Load GriefPrevention Plugin
		Plugin griefPreventionPlugin = getServer().getPluginManager().getPlugin("GriefPrevention");
		if(griefPreventionPlugin == null || !griefPreventionPlugin.isEnabled()) {
			getLogger().severe("GriefPrevention-Territory-Display was unable to find GriefPrevention dependency - Disabling");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		this.griefPreventionPlugin = (GriefPrevention) griefPreventionPlugin;

		//Load Config
		//TODO

		//Run Loop
		startLoop();

		//Log
		getServer().broadcastMessage("Enabled GriefPrevention-Territory-Display");
		getLogger().info("Enabled GriefPrevention-Territory-Display");
	}

	private void startLoop() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::loop, LOOP_DELAY, LOOP_PERIOD);
//		BukkitRunnable runnable = new BukkitRunnable() { public void run() { loop(); } };
//		runnable.runTask(this, LOOP_DELAY, LOOP_PERIOD);
	}

	private void loop() {
		Collection<Claim> claims = griefPreventionPlugin.dataStore.getClaims();

		for (Claim claim : claims) {
			Block block = getServer().getWorld("world").getBlockAt(-109, 107, 14);
			block.setType(Material.OAK_WALL_SIGN);

			String ownerName = claim.getOwnerName();
			Location coordA = claim.getLesserBoundaryCorner(), coordB = claim.getGreaterBoundaryCorner();

			Sign sign = (Sign) block.getState();
			sign.setLine(0, ownerName + "'s");
			sign.setLine(1, "Claim");
			sign.setLine(2, locationToCoordinateString(coordA));
			sign.setLine(3, locationToCoordinateString(coordB));
			sign.update();
		}

//		getServer().broadcastMessage();
	}

	private String locationToCoordinateString(Location location) {
		return "x: " + location.getBlockX() + ", z: " + location.getBlockZ();
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
	}
}
