package me.liamsnow.signsethome;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GiveWarpLobbySignCommand implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		SignSetHome.instance.getServer().broadcastMessage("Place as many of these signs in the Warp Lobby for players to use!");

		return true;
	}
}
