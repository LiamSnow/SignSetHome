package me.liamsnow.signsethome;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SignSetHome extends JavaPlugin {
	public static SignSetHome instance;
	public static GriefPrevention griefPrevention;

	@Override
	public void onEnable() {
		instance = this;

		//Load Config & Data
		ConfigHandler.init();
		DataHandler.init();

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
		getCommand("signsethome-reload").setExecutor(new ConfigHandler());
		getCommand("signsethome-setspawn").setExecutor(new SetSpawnCommand());
		getCommand("signsethome-setwarplobby").setExecutor(new SetWarpLobbyCommand());
		getCommand("signsethome-givewarplobbysign").setExecutor(new GiveWarpLobbySignCommand());

		//Register Event Handlers
		getServer().getPluginManager().registerEvents(new SignClickHandler(), this);

		//Log
		getLogger().info("Enabled SignSetHome!");
	}

	@Override
	public void onDisable() {
		ConfigHandler.save();
		DataHandler.save();
	}
}
