package me.liamsnow.griefpreventionterritorydisplay;

import me.liamsnow.signsethome.ConfigHandler;
import me.liamsnow.signsethome.SignSetHome;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.MetadataValue;

import java.io.File;
import java.util.List;

import static me.liamsnow.signsethome.Constants.SIGN_WARP_HOME_META_KEY;
import static me.liamsnow.signsethome.Constants.SIGN_WARP_SPAWN_META_KEY;

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
		player.teleport(ConfigHandler.getSpawnLocation());
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
