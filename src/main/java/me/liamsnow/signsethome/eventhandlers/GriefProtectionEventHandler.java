package me.liamsnow.signsethome.eventhandlers;

import me.liamsnow.signsethome.Constants;
import me.liamsnow.signsethome.SignSetHome;
import me.liamsnow.signsethome.filehandlers.DataFileHandler;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.PlayerData;
import me.ryanhamshire.GriefPrevention.events.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class GriefProtectionEventHandler implements Listener {

	//Send Info Message @ Claim Created
	@EventHandler
	public void claimCreatedEvent(ClaimCreatedEvent event) {
		CommandSender creator = event.getCreator();
		Bukkit.getScheduler().runTaskLater(SignSetHome.instance, () -> creator.sendMessage(ChatColor.GREEN + "Use /sethome to warp between here and spawn!"), 2L);
	}

	//Delete Homes @ Claim Deleted/Expired
	@EventHandler
	public void claimDeletedEvent(ClaimDeletedEvent event) {
		removeAllPlayerHomesInClaim(event.getClaim(), event.getClaim().getOwnerID());
	}
	@EventHandler
	public void claimExpirationEvent(ClaimExpirationEvent event) {
		removeAllPlayerHomesInClaim(event.getClaim(), null);
	}
	public void removeAllPlayerHomesInClaim(Claim claim, UUID responsiblePlayerUUID) {
		//Loop through every player with a home in this claim
		List<UUID> effectedPlayerUUIDs = DataFileHandler.getAllPlayersWithHomesInClaim(claim.getID());
		for (UUID effectedPlayerUUID : effectedPlayerUUIDs) {
			//Remove their home
			removePlayerHome(effectedPlayerUUID, responsiblePlayerUUID);
		}
	}

	//Delete Invalid Homes @ Claim Resized
	@EventHandler
	public void claimChangeEvent(ClaimChangeEvent event) {
		Claim claim = event.getTo();
		UUID responsiblePlayerUUID = event.getFrom().getOwnerID();

		//Loop through every player with a home in this claim
		List<UUID> effectedPlayerUUIDs = DataFileHandler.getAllPlayersWithHomesInClaim(claim.getID());
		for (UUID effectedPlayerUUID : effectedPlayerUUIDs) {
			//Remove the homes of people who are no longer in the claim
			Location effectPlayerHomeLocation = DataFileHandler.getHomeLocation(effectedPlayerUUID);
			if (!claim.contains(effectPlayerHomeLocation, true, false)) {
				removePlayerHome(effectedPlayerUUID, responsiblePlayerUUID);
			}
		}
	}

	//Delete Invalid Homes @ Claim Trust Changed
	@EventHandler
	public void untrustEvent(TrustChangedEvent event) {
		if (event.isGiven()) return;

		//Get Event Data
		UUID responsiblePlayerUUID = event.getChanger().getUniqueId();
		Collection<Claim> effectedClaims = event.getClaims();
		String effectedPlayersString = event.getIdentifier();

		//Player untrusted everyone :(
		if (effectedPlayersString.equalsIgnoreCase("all") || effectedPlayersString.equalsIgnoreCase("public")) {
			//Loop through every changed claim
			for (Claim effectedClaim : effectedClaims) {
				//Loop through every player with a home in this effected claim
				List<UUID> effectedPlayerUUIDs = DataFileHandler.getAllPlayersWithHomesInClaim(effectedClaim.getID());
				for (UUID effectedPlayerUUID : effectedPlayerUUIDs) {
					//Remove all the homes in the claim (except owner's)
					if (effectedPlayerUUID.compareTo(responsiblePlayerUUID) != 0) {
						removePlayerHome(effectedPlayerUUID, responsiblePlayerUUID);
					}
				}
			}
		}

		//Player untrusted one player :(((
		else {
			//Check if effect player's home is in one of the effected claims
			UUID effectedPlayerUUID = UUID.fromString(effectedPlayersString);
			long effectedPlayerHomeClaimID = DataFileHandler.getGriefPreventionClaimID(effectedPlayerUUID);
			for (Claim effectedClaim : effectedClaims) {
				//Effect player's home is in effected claim --> Remove it
				if (effectedPlayerHomeClaimID == effectedClaim.getID()) {
					removePlayerHome(effectedPlayerUUID, responsiblePlayerUUID);
				}
			}
		}
	}

	//Remove the Home of a Player & Send Messages
	private void removePlayerHome(UUID playerUUID, UUID responsiblePlayerUUID) {
		//Remove Home Sign
		if (DataFileHandler.hasValidHomeLocation(playerUUID)) {
			DataFileHandler.getHomeLocation(playerUUID).getBlock().setType(Constants.REPLACE_OLD_HOME_MATERIAL);
		}

		//Removed Saved Home Location
		DataFileHandler.removeHomeLocation(playerUUID);

		//Player Removing their Own Home
		Player player = Bukkit.getPlayer(playerUUID);
		if (responsiblePlayerUUID != null && playerUUID.compareTo(responsiblePlayerUUID) == 0) {
			//Notify the Player they removed their own home
			player.sendMessage(ChatColor.RED + "You're decision making led to your home being destroyed.");
		}

		//Player Removing other Player's Home
		else {
			//Notify the Player that their home was removed
			if (player != null) {
				player.sendMessage(ChatColor.RED + "As a result of poor territorial leadership, you're set home was removed.");
			}

			//Notify the Devil of the homes and lives they ruined :(
			if (responsiblePlayerUUID != null) {
				Player responsiblePlayer = Bukkit.getPlayer(responsiblePlayerUUID);
				OfflinePlayer effectedPlayer = Bukkit.getOfflinePlayer(playerUUID);
				String effectedPlayerName = effectedPlayer == null ? "Unknown" : effectedPlayer.getName();
				if (responsiblePlayer != null) {
					responsiblePlayer.sendMessage(ChatColor.RED + "You destroyed the peaceful and loving home of " + effectedPlayerName);
				}
			}
		}
	}
}
