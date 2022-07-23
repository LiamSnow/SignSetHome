package me.liamsnow.signsethome.commands;

import me.liamsnow.signsethome.*;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SetHomeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player)) return false;

		//Get Command Data
		Player player = (Player) sender;
		Location playerLocation = player.getLocation();
		Block playerBlock = playerLocation.getBlock();
		Material playerBlockMaterial = playerBlock.getType();

		//Force Intra-territory Set Homes
		boolean inOwnTerritory = false;
		boolean inWilderness = true;
		DataStore gpDataStore = SignSetHome.griefPrevention.dataStore;
		PlayerData gpPlayerData = gpDataStore.getPlayerData(player.getUniqueId());
		Claim gpClaim = gpDataStore.getClaimAt(playerLocation, false, gpPlayerData.lastClaim);

		if (gpClaim != null) {
			inWilderness = false;
			gpPlayerData.lastClaim = gpClaim;
			Supplier<String> supplier = gpClaim.checkPermission(player, ClaimPermission.Build,
			     new BlockPlaceEvent(playerBlock, playerBlock.getState(), playerBlock, new ItemStack(Constants.HOME_SIGN_MATERIAL), player, true, EquipmentSlot.HAND));
			inOwnTerritory = supplier == null;
		}

		if (!inOwnTerritory) {
			StringBuilder message = new StringBuilder();

			player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Error: " + ChatColor.RED +
				"You must place your home inside your territory or territory that you're trusted in. " + ChatColor.GRAY + "" + ChatColor.ITALIC +
				(inWilderness ? "You can claim territory by right clicking a piece of paper." :
				"Have the owner trust you by using /trust " + player.getDisplayName() + ".")
			);
			return true;
		}

		//Force Player to not be inside Block
		if (!playerBlockMaterial.isAir()) {
			player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Error: " + ChatColor.RESET + "" + ChatColor.RED +
					                   "You cannot place your home inside a block.");
			return true;
		}

		//Force Sign to be on Block
		Material belowPlayerBlockMaterial = playerBlock.getRelative(BlockFace.DOWN).getType();
		if (!belowPlayerBlockMaterial.isSolid() || Tag.TRAPDOORS.isTagged(belowPlayerBlockMaterial) || Tag.DOORS.isTagged(belowPlayerBlockMaterial)) {
			player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Error: " + ChatColor.RESET + "" + ChatColor.RED +
					                   "You must place your home above a solid block.");
			return true;
		}

		//Remove Old Set Home
		if (DataHandler.hasValidHomeLocation(player)) {
			DataHandler.getHomeLocation(player).getBlock().setType(Constants.REPLACE_OLD_HOME_MATERIAL);
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Removed old home.");
		}

		//Place Sign at Feet
		org.bukkit.block.data.type.Sign signData = (org.bukkit.block.data.type.Sign) Bukkit.createBlockData(Constants.HOME_SIGN_MATERIAL);
		signData.setRotation(Util.yawToFace(playerLocation.getYaw()));
		playerBlock.setBlockData(signData);

		//Tag Sign as Warp Spawn
		Sign sign = (Sign) playerBlock.getState();
		PersistentDataContainer signPersistentData = sign.getPersistentDataContainer();
		signPersistentData.set(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_TAG_KEY), PersistentDataType.INTEGER, Constants.TAG_SIGN_WARP_SPAWN);
		signPersistentData.set(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_UUID_KEY), PersistentDataType.STRING, player.getUniqueId().toString());

		//Set Sign Text
		sign.setLine(0, ChatColor.GREEN + "Warp to");
		sign.setLine(1, ChatColor.GOLD + "" + ChatColor.BOLD + "Spawn");
		sign.setLine(2, "");
		sign.setLine(3, "");
		sign.update();

		//Save New Set Home Location
		DataHandler.saveHomeLocation(player, playerLocation);

		//Has Warp Sign
		if (DataHandler.hasValidWarpSignLocation(player)) {
			player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Set new home!" + ChatColor.RESET + "" + ChatColor.YELLOW +
					                   " You can teleport to Spawn with the sign at your feet and teleport back here with your claimed sign at Spawn.");
		}

		//Needs to Claim Warp Sign
		else {
			//Teleport to Warp Lobby
			player.teleport(ConfigHandler.getWarpLobbyLocation());

			//Send Message
			player.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "Set Home!", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Right-Click to Claim a Warp Sign", 10, 70, 20);
			player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Set new home!" + ChatColor.YELLOW + "" + ChatColor.ITALIC +
					                   " Right-Click to Claim a Warp Sign at Spawn. You can teleport to your new home with your claimed sign and back to spawn with the sign at your home.");

		}

		return true;
	}
}
