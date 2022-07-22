package me.liamsnow.signsethome;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import static me.liamsnow.signsethome.Constants.SIGN_WARP_SPAWN_META_KEY;

public class SetHomeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (sender instanceof Player) {
			//Get Command Data
			Player player = (Player) sender;
			Location playerLocation = player.getLocation();
			Block playerBlock = playerLocation.getBlock();

			//Force Intra-territory Set Homes
			//TODO

			//Remove Old Set Home
			Location oldSetHome = DataHandler.getSetHome(player);
			if (oldSetHome != null) {
				Block oldSetHomeBlock = Util.getOverworld().getBlockAt(oldSetHome);
				if (oldSetHomeBlock.getState() instanceof Sign) {
					oldSetHomeBlock.setType(Constants.REPLACE_OLD_HOME_MATERIAL);
					SignSetHome.instance.getServer().broadcastMessage("REPLACED OLD HOME: " + oldSetHome.toString());
				}
				else SignSetHome.instance.getServer().broadcastMessage("HAS OLD HOME, bUT NOT SIGN");
			}

			//If Player is standing in block, Drop It
			Material blockMaterial = playerBlock.getType();
			if (blockMaterial.isBlock()) {
				ItemStack blockAsItem = new ItemStack(blockMaterial);
				Util.getOverworld().dropItem(playerLocation, blockAsItem);
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

		return false;
	}
}
