package me.liamsnow.signsethome.commands;

import me.liamsnow.signsethome.filehandlers.ConfigFileHandler;
import me.liamsnow.signsethome.filehandlers.DataFileHandler;
import me.liamsnow.signsethome.SignSetHome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		sender.sendMessage("Reloading SignSetHome Config & Data!");

		ConfigFileHandler.load();
		DataFileHandler.load();

		return true;
	}

}
