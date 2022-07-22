package me.liamsnow.griefpreventionterritorydisplay;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

import static me.liamsnow.griefpreventionterritorydisplay.Constants.SIGN_WARP_HOME_META_KEY;
import static me.liamsnow.griefpreventionterritorydisplay.Constants.SIGN_WARP_SPAWN_META_KEY;

public class SignHandler implements Listener {

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		//Get Event Data
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		//Checks
		if (block == null) return;
		if (!(block.getState() instanceof Sign)) return;

		//Is Warp
		boolean isWarpSpawn = getFirstMetadataValueAsBoolean(block.getMetadata(SIGN_WARP_SPAWN_META_KEY));
		boolean isWarpHome = getFirstMetadataValueAsBoolean(block.getMetadata(SIGN_WARP_HOME_META_KEY));
		if (!isWarpSpawn && !isWarpHome) return;

		//Teleport Them
		player.teleport(new Location(player.getWorld(), -101.5, 107, 15.5, 180, 0));
	}

	private boolean getFirstMetadataValueAsBoolean(List<MetadataValue> values) {
		int size = values.size();
		if (size == 0) return false;
		else {
			if (size > 1) GriefPreventionTerritoryDisplay.instance.getLogger().warning("GriefPrevention-Territory-Display Error - Multiple Metadatas on Sign");
			return values.get(0).asBoolean();
		}
	}

}
