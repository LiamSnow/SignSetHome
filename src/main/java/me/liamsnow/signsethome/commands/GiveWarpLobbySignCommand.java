package me.liamsnow.signsethome.commands;

import me.liamsnow.signsethome.Constants;
import me.liamsnow.signsethome.SignSetHome;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class GiveWarpLobbySignCommand implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		//Create Sign Item
        ItemStack signItem = new ItemStack(Constants.WARP_HOME_SIGN_MATERIAL, 1);
		ItemMeta signItemMeta = signItem.getItemMeta();

		//Set Name + Lore
		signItemMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Warp Lobby Sign");
		signItemMeta.setLore(Arrays.asList("Place as many of these signs in the", "Warp Lobby for players to use!"));

		//Get Block Data
		BlockStateMeta signBlockStateMeta = (BlockStateMeta) signItemMeta;
		Sign signBlockState = (Sign) signBlockStateMeta.getBlockState();

		//Tag Sign as Warp Spawn
		PersistentDataContainer signPersistentData = signBlockState.getPersistentDataContainer();
		signPersistentData.set(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_TAG_KEY), PersistentDataType.INTEGER, Constants.TAG_SIGN_WARP_HOME_UNCLAIMED);

		//Edit Sign Text
		signBlockState.setLine(0, ChatColor.RED + "Right Click");
		signBlockState.setLine(1, ChatColor.GRAY + "to Claim this");
		signBlockState.setLine(2, ChatColor.GRAY + "Set Home Sign");
		signBlockState.update();


		//Save Item & Block Data
		signBlockStateMeta.setBlockState(signBlockState);
		signItem.setItemMeta(signBlockStateMeta);

		//Add Item to Player's Inventory
		player.getInventory().addItem(signItem);

		//Send message to Player
		SignSetHome.instance.getServer().broadcastMessage(ChatColor.GREEN + "Given Warp Lobby Sign. " + ChatColor.GRAY + "" + ChatColor.ITALIC +
				                                                  "Place as many of these signs in the Warp Lobby for players to use!");

		return true;
	}
}
