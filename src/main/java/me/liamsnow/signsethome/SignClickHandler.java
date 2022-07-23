package me.liamsnow.signsethome;

import me.liamsnow.signsethome.ConfigHandler;
import me.liamsnow.signsethome.SignSetHome;
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
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		//Get Event Data
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		//Checks
		if (block == null) return;
		if (!(block.getState() instanceof Sign)) return;

		//Check if Warp
		PersistentDataContainer signPersistentData = ((Sign) block.getState()).getPersistentDataContainer();
		int signPersistentDataValue = signPersistentData.get(new NamespacedKey(SignSetHome.instance, Constants.PERSISTENT_DATA_KEY), PersistentDataType.INTEGER);
		boolean isWarpSpawn = signPersistentDataValue == Constants.TAG_SIGN_WARP_SPAWN;

		player.sendMessage(signPersistentDataValue + "," + (signPersistentDataValue == Constants.TAG_SIGN_WARP_HOME_UNCLAIMED));

		boolean isClaimedWarpHome = signPersistentDataValue == Constants.TAG_SIGN_WARP_HOME_CLAIMED;
		if (!isWarpSpawn && !isClaimedWarpHome) return;

		//Teleport Them
		if (isWarpSpawn) {
			player.teleport(ConfigHandler.getSpawnLocation());
		}
		else {
			//TODO
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
