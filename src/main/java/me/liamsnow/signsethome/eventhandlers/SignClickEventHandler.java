package me.liamsnow.signsethome.eventhandlers;

import me.liamsnow.signsethome.Constants;
import me.liamsnow.signsethome.SignSetHome;
import me.liamsnow.signsethome.Util;
import me.liamsnow.signsethome.filehandlers.ConfigFileHandler;
import me.liamsnow.signsethome.filehandlers.DataFileHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class SignClickEventHandler implements Listener {

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
		int signTag = signPersistentData.getOrDefault(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_TAG_KEY), PersistentDataType.INTEGER, -1);
		String signOwnerUUIDString = signPersistentData.getOrDefault(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_UUID_KEY), PersistentDataType.STRING, null);
		UUID signOwnerUUID = signOwnerUUIDString == null ? null : UUID.fromString(signOwnerUUIDString);

		//Warp Spawn
		if (signTag == Constants.TAG_SIGN_WARP_SPAWN) {
			//Owner has no valid Warp Sign
			if (!DataFileHandler.hasValidWarpSignLocation(signOwnerUUID)) {
				onInvalidSign(player, "Owner has not claimed a warp home sign at spawn!");
			}

			//All Good -- Warp the Player
			else Util.warp(player, ConfigFileHandler.getSpawnLocation(), "Spawn!");
		}

		//Warp Home
		if (signTag == Constants.TAG_SIGN_WARP_HOME_CLAIMED) {
			//Sign has no Saved Owner UUID
			if (signOwnerUUID == null) {
				onInvalidSign(player, "Owner does not exist!");
			}

			//Owner has Invalid or No Home
			else if (!DataFileHandler.hasValidHomeLocation(signOwnerUUID)) {
				onInvalidSign(player, "Owner has no valid home!");
			}

			//All Good -- Warp the Player
			else Util.warp(player, DataFileHandler.getHomeLocation(signOwnerUUID), getWarpSignMessage(player, signOwnerUUID));
		}

		//Claim Warp Sign
		if (signTag == Constants.TAG_SIGN_WARP_HOME_UNCLAIMED) {
			//Player has no Valid Home
			if (!DataFileHandler.hasValidHomeLocation(player)) {
				player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Error: " + ChatColor.RED +
						                   "You must have a home first. " + ChatColor.GRAY + "" + ChatColor.ITALIC +
						                   "You can set a home by using /sethome in your territory or territory that you're trusted in. " +
						                   "You can claim territory by right clicking a piece of paper on the ground. " +
						                   "You can become trusted in a territory by having the owner run /trust " + player.getDisplayName() + "."
				);
			}

			//Player has already Claimed Warp Sign
			else if (DataFileHandler.hasValidWarpSignLocation(player)) {
				player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Error: " + ChatColor.RED +
						                   "You've already claimed a warp sign." + ChatColor.GRAY + "" + ChatColor.ITALIC +
						                   "If there is an issue please post it on the Discord under #issues"
				);
			}

			//All Good -- Let the player claim it
			else {
				//Tag Claimed Sign & Add UUID
				signPersistentData.set(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_TAG_KEY), PersistentDataType.INTEGER, Constants.TAG_SIGN_WARP_HOME_CLAIMED);
				signPersistentData.set(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_UUID_KEY), PersistentDataType.STRING, player.getUniqueId().toString());

				//Save Warp Spawn Location
				DataFileHandler.saveWarpSignLocation(player, sign.getLocation());

				//Set Sign Text
				sign.setLine(0, ChatColor.GREEN + "Warp to");
				sign.setLine(1, ChatColor.GOLD + "" + ChatColor.BOLD + player.getDisplayName() + "'s");
				sign.setLine(2, ChatColor.GOLD + "" + ChatColor.BOLD + "Home");
				sign.setLine(3, "");
				sign.update();
			}
		}
	}

	private void onInvalidSign(Player player, String reason) {
		player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Sign: " + ChatColor.RESET + "" + ChatColor.RED + reason);
		SignSetHome.instance.getLogger().warning("Error: Invalid SignSetHome Sign: " + reason);
	}

	private String getWarpSignMessage(Player usingPlayer, UUID ownerUUID) {
		if (ownerUUID.equals(usingPlayer.getUniqueId().toString())) {
			return "Your Home!";
		}
		else {
			return Util.getPlayerUsernameFromUUID(ownerUUID) + "'s Home!";
		}
	}
}
