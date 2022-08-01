package me.liamsnow.signsethome.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.liamsnow.signsethome.SignSetHome;
import me.liamsnow.signsethome.Util;
import me.liamsnow.signsethome.filehandlers.ConfigFileHandler;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.Messages;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

//An edited implementation of the original GriefPrevention /trapped command.
//The /stuck command works in both GriefPrevention land claims and a WorldGuard "Spawn" region.
//Partial Credit to GriefPrevention (Ryan Hamshire)
public class StuckCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		Location playerLocation = player.getLocation();
		World overworld = Util.getOverworld();

		//Make sure Player is in Overworld
		if (player.getWorld() != overworld) {
			player.sendMessage(ChatColor.RED + "Error: You cannot use this command outside the Overworld.");
		}

		//Make sure Player doesn't already have pending /trapped or /stuck command
		PlayerData playerData = SignSetHome.griefPrevention.dataStore.getPlayerData(player.getUniqueId());
		if (playerData.pendingTrapped) {
			return true;
		}

		//Check with WorldGuard if they are in Spawn
		boolean playerInSpawn = false;
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(overworld));
		if (regions != null) {
			ProtectedRegion spawnRegion = regions.getRegion("spawn");
			playerInSpawn = spawnRegion.contains(playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ());
		}

		//In Spawn
		if (playerInSpawn) {
			//Send message
			GriefPrevention.sendMessage(player, ChatColor.YELLOW, Messages.RescuePending);

			//Create task to teleport after 10 seconds
			PlayerRescueTask task = new PlayerRescueTask(player, player.getLocation(), ConfigFileHandler.getSpawnLocation());
			SignSetHome.instance.getServer().getScheduler().scheduleSyncDelayedTask(SignSetHome.instance, task, 200L);

			//Mark the player as pending teleport
			playerData.pendingTrapped = true;
		}

		//Outside Spawn
		else {
			//This is GriefPreventions Job
			Bukkit.dispatchCommand(player, "trapped");
		}

		return true;
	}

	//tries to rescue a trapped player from a claim where he doesn't have permission to save himself
	//related to the /trapped slash command
	//this does run in the main thread, so it's okay to make non-thread-safe calls
	class PlayerRescueTask implements Runnable
	{
		//original location where /trapped was used
		private final Location location;

		//rescue destination, may be decided at instantiation or at execution
		private Location destination;

		//player data
		private final Player player;

		public PlayerRescueTask(Player player, Location location, Location destination)
		{
			this.player = player;
			this.location = location;
			this.destination = destination;
		}

		@Override
		public void run()
		{
			//if he logged out, don't do anything
			if (!player.isOnline()) return;

			//he no longer has a pending /trapped slash command, so he can try to use it again now
			PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
			playerData.pendingTrapped = false;

			//if the player moved three or more blocks from where he used /trapped, admonish him and don't save him
			if (!player.getLocation().getWorld().equals(this.location.getWorld()) || player.getLocation().distance(this.location) > 3)
			{
				GriefPrevention.sendMessage(player, ChatColor.RED, Messages.RescueAbortedMoved);
				return;
			}

			//otherwise find a place to teleport him
			if (this.destination == null)
			{
				this.destination = GriefPrevention.instance.ejectPlayer(this.player);
			}
			else
			{
				player.teleport(this.destination);
			}

			//log entry, in case admins want to investigate the "trap"
			GriefPrevention.AddLogEntry("Rescued trapped player " + player.getName() + " from " + GriefPrevention.getfriendlyLocationString(this.location) + " to " + GriefPrevention.getfriendlyLocationString(this.destination) + ".");
		}
	}

}