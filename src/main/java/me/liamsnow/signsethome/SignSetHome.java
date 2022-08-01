package me.liamsnow.signsethome;

import com.sk89q.worldguard.WorldGuard;
import me.liamsnow.signsethome.commands.*;
import me.liamsnow.signsethome.eventhandlers.GriefProtectionEventHandler;
import me.liamsnow.signsethome.eventhandlers.SignBreakEventHandler;
import me.liamsnow.signsethome.eventhandlers.SignClickEventHandler;
import me.liamsnow.signsethome.filehandlers.ConfigFileHandler;
import me.liamsnow.signsethome.filehandlers.DataFileHandler;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public final class SignSetHome extends JavaPlugin {
	public static SignSetHome instance;
	public static GriefPrevention griefPrevention;

	@Override
	public void onEnable() {
		instance = this;

		//Load Config & Data
		ConfigFileHandler.init();
		DataFileHandler.init();

		//Load GriefPrevention Plugin
		Plugin griefPreventionPlugin = getServer().getPluginManager().getPlugin("GriefPrevention");
		if(griefPreventionPlugin == null || !griefPreventionPlugin.isEnabled()) {
			getLogger().severe("SignSetHome was unable to find GriefPrevention dependency - Disabling");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		griefPrevention = (GriefPrevention) griefPreventionPlugin;

		//Register Commands
		getCommand("sethome").setExecutor(new SetHomeCommand());
		getCommand("stuck").setExecutor(new StuckCommand());
		getCommand("signsethome-reload").setExecutor(new ReloadCommand());
		getCommand("signsethome-setspawn").setExecutor(new SetSpawnCommand());
		getCommand("signsethome-setwarplobby").setExecutor(new SetWarpLobbyCommand());
		getCommand("signsethome-givewarplobbysign").setExecutor(new GiveWarpLobbySignCommand());

		//Register Event Handlers
		getServer().getPluginManager().registerEvents(new SignClickEventHandler(), this);
		getServer().getPluginManager().registerEvents(new SignBreakEventHandler(), this);
		getServer().getPluginManager().registerEvents(new GriefProtectionEventHandler(), this);
		//TODO remove /sethome on claim abandonment

		//Log
		getLogger().info("Enabled SignSetHome!");
	}

	@Override
	public void onDisable() {
		ConfigFileHandler.save();
		DataFileHandler.save();
	}
}
