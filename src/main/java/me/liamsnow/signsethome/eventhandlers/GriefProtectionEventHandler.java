package me.liamsnow.signsethome.eventhandlers;

import me.liamsnow.signsethome.Constants;
import me.liamsnow.signsethome.SignSetHome;
import me.liamsnow.signsethome.filehandlers.DataFileHandler;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.events.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class GriefProtectionEventHandler implements Listener {

	@EventHandler
	public void claimDeletedEvent(ClaimDeletedEvent event) {
		removeAllPlayerHomesInClaim(event.getClaim().getID(), event.getClaim().getOwnerID());
	}

	@EventHandler
	public void claimExpirationEvent(ClaimExpirationEvent event) {
		removeAllPlayerHomesInClaim(event.getClaim().getID(), null);
	}

	public void removeAllPlayerHomesInClaim(long claimID, UUID responsiblePlayer) {
		List<UUID> effectedPlayerUUIDs = DataFileHandler.getAllPlayersWithHomesInClaim(claimID);

		for (UUID effectedPlayerUUID : effectedPlayerUUIDs) {
			removePlayerHome(effectedPlayerUUID, responsiblePlayer);
		}
	}

	@EventHandler
	public void claimResizeEvent(ClaimResizeEvent event) {
		verifyPlayerHomesInClaim(event.getTo().getID(), event.getFrom().getOwnerID());
	}

	@EventHandler
	public void claimChangeEvent(ClaimChangeEvent event) {
		verifyPlayerHomesInClaim(event.getTo().getID(), event.getFrom().getOwnerID());
	}

	@EventHandler
	public void trustChangedEvent(TrustChangedEvent event) {
		Collection<Claim> changedClaims = event.getClaims();

		for (Claim changedClaim : changedClaims) {
			verifyPlayerHomesInClaim(changedClaim.getID(), changedClaim.getOwnerID()));
		}
	}

	public void verifyPlayerHomesInClaim(long claimID, UUID responsiblePlayerUUID) {
		List<UUID> effectedPlayerUUIDs = DataFileHandler.getAllPlayersWithHomesInClaim(claimID);

		for (UUID effectedPlayerUUID : effectedPlayerUUIDs) {
			//TODO verify their home is still in land claim
		}
	}

	private void removePlayerHome(UUID playerUUID, UUID responsiblePlayerUUID) {
		//TODO

//		SignSetHome.instance.getServer().broadcastMessage("REMOVING THE HOME OF " + playerUUID);

		//Remove Home Sign
		if (DataFileHandler.hasValidHomeLocation(playerUUID)) {
			DataFileHandler.getHomeLocation(player).getBlock().setType(Constants.REPLACE_OLD_HOME_MATERIAL);
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Removed old home.");
		}

		//Removed Saved Home Location
		DataFileHandler.removeHomeLocation(playerUUID);

		//Notify the Player that their home was removed


		//Notify the Devil of the homes and lives they ruined :(
		if (responsiblePlayerUUID != null) {
			Player responsiblePlayer = Bukkit.getPlayer(responsiblePlayerUUID);
			if (responsiblePlayer != null) {
				responsiblePlayer.sendMessage(ChatColor.RED + "Removing the home of " + playerUUID);
			}
		}
	}
}
