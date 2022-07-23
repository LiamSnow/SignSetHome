package me.liamsnow.signsethome.commands;

import me.liamsnow.signsethome.ConfigHandler;
import me.liamsnow.signsethome.DataHandler;
import me.liamsnow.signsethome.SignSetHome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		SignSetHome.instance.getServer().broadcastMessage("Reloading SignSetHome Config & Data!");

		ConfigHandler.load();
		DataHandler.load();

		return true;
	}

}
