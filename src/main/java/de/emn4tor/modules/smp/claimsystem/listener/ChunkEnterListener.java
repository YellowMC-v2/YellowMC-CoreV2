package de.emn4tor.modules.smp.claimsystem.listener;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.smp.claimsystem.logic.ClaimManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event listener that notifies players when entering new territories.
 */
public class ChunkEnterListener implements Listener {

    private final ClaimManager claimManager;
    private final Map<UUID, String> lastTerritory = new HashMap<>(); // Tracks last territory per player

    public ChunkEnterListener(ClaimManager claimManager) {
        this.claimManager = claimManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Chunk fromChunk = event.getFrom().getChunk();
        Chunk toChunk = event.getTo().getChunk();

        // Only proceed if the player moved to a new chunk
        if (fromChunk.getX() == toChunk.getX() && fromChunk.getZ() == toChunk.getZ()
                && fromChunk.getWorld().equals(toChunk.getWorld())) {
            return;
        }

        String territoryName;

        if (claimManager.isClaimed(toChunk.getWorld().getName(), toChunk.getX(), toChunk.getZ())) {
            UUID ownerUUID = claimManager.getClaim(
                    toChunk.getWorld().getName(),
                    toChunk.getX(),
                    toChunk.getZ()
            ).getOwnerUUID();

            OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUUID);
            String unknownTerritory = YellowMCCoreV2.getTranslationService().translate(player.getUniqueId(), "claims-enter-territory-unknown");
            String territoryWithName = YellowMCCoreV2.getTranslationService().translate(player.getUniqueId(), "claims-enter-territory", Map.of("0", owner.getName()));
            territoryName = owner.getName() != null ? territoryWithName : unknownTerritory;
        } else {
            territoryName = "Wilderness";
        }

        // Only send message if player is entering a new territory
        if (!territoryName.equals(lastTerritory.get(player.getUniqueId()))) {
            String enteringText = YellowMCCoreV2.getTranslationService().translate(player.getUniqueId(), "claims-enter-text");
            player.sendActionBar(MiniMessage.miniMessage().deserialize(enteringText + " <yellow>" + territoryName + "</yellow>"));
            lastTerritory.put(player.getUniqueId(), territoryName);
        }
    }
}
