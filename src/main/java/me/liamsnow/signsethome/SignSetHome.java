package me.liamsnow.signsethome;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SignSetHome extends JavaPlugin {
	public static SignSetHome instance;

	private GriefPrevention griefPreventionPlugin;

	@Override
	public void onEnable() {
		instance = this;

		//Load Config
		ConfigHandler.loadConfig();

		//Load GriefPrevention Plugin
		Plugin griefPreventionPlugin = getServer().getPluginManager().getPlugin("GriefPrevention");
		if(griefPreventionPlugin == null || !griefPreventionPlugin.isEnabled()) {
			getLogger().severe("SignSetHome was unable to find GriefPrevention dependency - Disabling");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		this.griefPreventionPlugin = (GriefPrevention) griefPreventionPlugin;

		//Register Set Home Command
		this.getCommand("sethome").setExecutor(new SetHomeCommand());

		//Register Sign Event Handler
		getServer().getPluginManager().registerEvents(new SignHandler(), this);

		//Register Event for @ Territory Claim

		//Log
		getLogger().info("Enabled SignSetHome");
	}

	@Override
	public void onDisable() {

	}
}
