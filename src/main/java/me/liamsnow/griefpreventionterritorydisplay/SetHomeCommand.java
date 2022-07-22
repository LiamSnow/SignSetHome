package me.liamsnow.griefpreventionterritorydisplay;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
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

import static me.liamsnow.griefpreventionterritorydisplay.Constants.SIGN_WARP_SPAWN_META_KEY;

public class SetHomeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (sender instanceof Player) {
			//Get Command Data
			Player player = (Player) sender;
			World world = player.getWorld();
			Location location = player.getLocation();
			Block block = location.getBlock();

			//Force Overworld Set Homes
//			if (world.getEnvironment() != Environment.NORMAL) {
//				player.sendMessage("You must be in the overworld to set your home!");
//				return false;
//			}

			//Force Intra-territory Set Homes
			//TODO

			//If Player is standing in block, Drop It
			Material blockMaterial = block.getType();
			if (blockMaterial.isBlock()) {
				ItemStack blockAsItem = new ItemStack(blockMaterial);
				world.dropItem(location, blockAsItem);
			}

			//Place Sign at Feet
			block.setType(Material.OAK_SIGN);
			block.setMetadata(SIGN_WARP_SPAWN_META_KEY,
			                  new FixedMetadataValue(GriefPreventionTerritoryDisplay.instance, true));

			//Set Sign Text
			Sign sign = (Sign) block.getState();
			sign.setLine(0, "" + ChatColor.GREEN + "Warp");
			sign.setLine(1, "" + ChatColor.GREEN + "to");
			sign.setLine(2, "" + ChatColor.BOLD + ChatColor.GOLD + "Spawn");
			sign.setLine(3, "");
			sign.update();

			//Save Location to Yaml

			return true;
		}

		return false;
	}
}
