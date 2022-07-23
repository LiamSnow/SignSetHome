package me.liamsnow.signsethome;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class SignClickHandler implements Listener {

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		//Get Event Data
		Action action = event.getAction();
		Player player = event.getPlayer();
		Block signBlock = event.getClickedBlock();

		//Checks
		if (action != Action.RIGHT_CLICK_BLOCK) return;
		if (signBlock == null) return;
		if (!(signBlock.getState() instanceof Sign)) return;

		//Get Sign
		Sign sign = (Sign) signBlock.getState();

		//Check if Warp
		PersistentDataContainer signPersistentData = sign.getPersistentDataContainer();
		int signPersistentDataValue = signPersistentData.get(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_TAG_KEY), PersistentDataType.INTEGER);

		//Warp Spawn
		if (signPersistentDataValue == Constants.TAG_SIGN_WARP_SPAWN) {
			player.teleport(ConfigHandler.getSpawnLocation());
			return;
		}

		//Warp Home
		if (signPersistentDataValue == Constants.TAG_SIGN_WARP_HOME_CLAIMED) {
			String UUID = signPersistentData.get(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_UUID_KEY), PersistentDataType.STRING);

			if (UUID == null) {
				player.sendMessage(ChatColor.RED + "Invalid Warp Sign -- No Player UUID Saved");
				SignSetHome.instance.getLogger().severe("Error: Invalid Warp Sign -- No Player UUID Saved");
				return;
			}

			Location homeLocation = DataHandler.getHomeLocation(UUID);

			if (homeLocation == null) {
				player.sendMessage(ChatColor.RED + "Invalid Warp Sign -- No Player Home Saved");
				SignSetHome.instance.getLogger().severe("Error: Invalid Warp Sign -- No Player Home Saved");
				return;
			}

			player.teleport(homeLocation);
			return;
		}

		//Claim Warp Sign
		if (signPersistentDataValue == Constants.TAG_SIGN_WARP_HOME_UNCLAIMED) {
			if (!Util.isSignAtLocation(DataHandler.getHomeLocation(player))) {
				player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Error: " + ChatColor.RED +
						                   "You must have a home first. " + ChatColor.GRAY + "" + ChatColor.ITALIC +
						                   "You can set a home by using /sethome in your territory or territory that you're trusted in. " +
						                   "You can claim territory by right clicking a piece of paper on the ground. " +
						                   "You can become trusted in a territory by having the owner run /trust " + player.getDisplayName() + "."
				);
				return;
			}

			if (Util.isSignAtLocation(DataHandler.getWarpSignLocation(player))) {
				player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Error: " + ChatColor.RED +
						                   "You've already claimed a warp sign." + ChatColor.GRAY + "" + ChatColor.ITALIC +
						                   "If there is an issue please post it on the Discord under #issues"
				);
				return;
			}

			//Tag Claimed Sign & Add UUID
			signPersistentData.set(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_TAG_KEY), PersistentDataType.INTEGER, Constants.TAG_SIGN_WARP_HOME_CLAIMED);
			signPersistentData.set(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_UUID_KEY), PersistentDataType.STRING, player.getUniqueId().toString());

			//Save Warp Spawn Location
			DataHandler.saveWarpSignLocation(player, sign.getLocation());

			//Set Sign Text
			sign.setLine(0, ChatColor.GREEN + "Warp to");
			sign.setLine(1, ChatColor.GOLD + "" + ChatColor.BOLD + player.getDisplayName() + "'s");
			sign.setLine(2, ChatColor.GOLD + "" + ChatColor.BOLD + "Home");
			sign.setLine(3, "");
			sign.update();

			return;
		}
	}

	private boolean getFirstMetadataValueAsBoolean(List<MetadataValue> values) {
		int size = values.size();
		if (size == 0) return false;
		else {
			if (size > 1) SignSetHome.instance.getLogger().warning("SignSetHome Error - Multiple Metadatas on Sign");
			return values.get(0).asBoolean();
		}
	}

}
