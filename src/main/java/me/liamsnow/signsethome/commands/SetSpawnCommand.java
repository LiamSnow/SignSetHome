package me.liamsnow.signsethome.commands;

import me.liamsnow.signsethome.ConfigHandler;
import me.liamsnow.signsethome.SignSetHome;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetSpawnCommand implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		ConfigHandler.setSpawnLocation(player.getLocation());
		SignSetHome.instance.getServer().broadcastMessage(ChatColor.GREEN + "Set SignSetHome Spawn!");

		return true;
	}
}
