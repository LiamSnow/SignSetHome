package me.liamsnow.signsethome.eventhandlers;

import me.liamsnow.signsethome.Constants;
import me.liamsnow.signsethome.SignSetHome;
import me.liamsnow.signsethome.filehandlers.DataFileHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SignBreakEventHandler implements Listener {

	@EventHandler
	public void onSignBreak(BlockBreakEvent event) {
		//Get Event Data
		Player player = event.getPlayer();
		Block signBlock = event.getBlock();

		//Checks
		if (signBlock == null) return;
		if (!(signBlock.getState() instanceof Sign)) return;

		//Get Sign
		Sign sign = (Sign) signBlock.getState();

		//Check if Warp
		PersistentDataContainer signPersistentData = sign.getPersistentDataContainer();
		int signTag = signPersistentData.getOrDefault(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_TAG_KEY), PersistentDataType.INTEGER, -1);
		String signUUID = signPersistentData.getOrDefault(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_UUID_KEY), PersistentDataType.STRING, null);

		if (signTag == Constants.TAG_SIGN_WARP_SPAWN) {
			//Breaking their own Set Home
			if (signUUID.equals(player.getUniqueId().toString())) {
				event.setDropItems(false);
				DataFileHandler.saveHomeLocation(player, new Location(player.getWorld(), Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, 0f, 0f));
				player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Set Home Removed. " + ChatColor.RESET + "" + ChatColor.GRAY + "" + ChatColor.ITALIC +
						                   "Add it back by using /sethome in your territory or territory you are trusted in.");
			}

			//Breaking other Set Home
			else {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Error: " + ChatColor.RESET + "" + ChatColor.RED +
						                   "You cannot remove someone else's Set Home.");
			}
		}
	}

}
