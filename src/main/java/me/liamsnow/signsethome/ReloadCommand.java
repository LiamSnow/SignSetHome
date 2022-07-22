package me.liamsnow.signsethome;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ReloadCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		SignSetHome.instance.getServer().broadcastMessage("Reloading SignSetHome Config!");
		ConfigHandler.loadConfig();
		return true;
	}

}
