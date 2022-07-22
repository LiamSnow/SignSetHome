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
	public static GriefPreventionTerritoryDisplay instance;

	private GriefPrevention griefPreventionPlugin;

	@Override
	public void onEnable() {
		instance = this;

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

		//Register Set Home Command
		this.getCommand("sethome").setExecutor(new SetHomeCommand());

		//Register Sign Event Handler
		getServer().getPluginManager().registerEvents(new SignHandler(), this);

		//Log
		getServer().broadcastMessage("Enabled GriefPrevention-Territory-Display");
		getLogger().info("Enabled GriefPrevention-Territory-Display");
	}

	//Register Event for @ Territory Claim


}
