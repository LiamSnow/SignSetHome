package me.liamsnow.signsethome;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static me.liamsnow.signsethome.Constants.SIGN_WARP_SPAWN_META_KEY;

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

			player.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "Error: " + ChatColor.RED +
				"You must place your home inside your territory or territory that you're trusted in. " + ChatColor.GRAY + "" + ChatColor.ITALIC +
				(inWilderness ? "You can claim territory by right clicking a piece of paper." :
				"Have the owner trust you by using /trust " + player.getDisplayName() + ".")
			);
			return true;
		}

		//Force Player to not be inside Block
		if (!playerBlockMaterial.isAir()) {
			player.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "Error: " + ChatColor.RESET + ChatColor.RED +
					                   "You cannot place your home inside a block.");
			return true;
		}

		//Force Sign to be on Block
		Material belowPlayerBlockMaterial = playerBlock.getRelative(BlockFace.DOWN).getType();
		if (!belowPlayerBlockMaterial.isSolid() || Tag.TRAPDOORS.isTagged(belowPlayerBlockMaterial) || Tag.DOORS.isTagged(belowPlayerBlockMaterial)) {
			player.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "Error: " + ChatColor.RESET + ChatColor.RED +
					                   "You must place your home above a solid block.");
			return true;
		}

		//Remove Old Set Home
		Location oldSetHome = DataHandler.getSetHome(player);
		if (oldSetHome != null) {
			Block oldSetHomeBlock = Util.getOverworld().getBlockAt(oldSetHome);
			if (oldSetHomeBlock.getState() instanceof Sign) {
				oldSetHomeBlock.setType(Constants.REPLACE_OLD_HOME_MATERIAL);
				player.sendMessage(ChatColor.GRAY + "Removed old home.");
			}
		}

		//Place Sign at Feet
		org.bukkit.block.data.type.Sign signData = (org.bukkit.block.data.type.Sign) Bukkit.createBlockData(Constants.HOME_SIGN_MATERIAL);
		signData.setRotation(Util.yawToFace(playerLocation.getYaw()));
		playerBlock.setBlockData(signData);
		playerBlock.setMetadata(SIGN_WARP_SPAWN_META_KEY,
		                  new FixedMetadataValue(SignSetHome.instance, true));

		//Set Sign Text
		Sign sign = (Sign) playerBlock.getState();
		sign.setLine(0, "" + ChatColor.GREEN + "Warp");
		sign.setLine(1, "" + ChatColor.GREEN + "to");
		sign.setLine(2, "" + ChatColor.BOLD + ChatColor.GOLD + "Spawn");
		sign.setLine(3, "");
		sign.update();

		//Save New Set Home Location
		DataHandler.saveSetHome(player, playerLocation);

		//Send Message
		player.sendMessage("" + ChatColor.GOLD + ChatColor.BOLD + "Set new home!" + ChatColor.RESET + ChatColor.YELLOW + " You can teleport to Spawn with the sign at your feet and teleport back here with the sign at Spawn.");

		return true;
	}
}
