package de.emn4tor.modules.global.sync.listener;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.models.PlayerSyncPacket;
import de.emn4tor.api.player.SyncService;
import de.emn4tor.modules.global.sync.SyncPacketUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class TriggerSyncEvent implements Listener {
    private final SyncService syncService = YellowMCCoreV2.getSyncService();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        PlayerSyncPacket packet = SyncPacketUtil.createSyncPacket(player);

        syncService.lockPlayerData(player.getUniqueId());

        syncService.savePlayerData(player.getUniqueId(), packet).thenRun(() -> {
            YellowMCCoreV2.getInstance().getLogger().info("Saved sync data for player " + player.getName());
        }
        ).exceptionally(ex -> {
            YellowMCCoreV2.getInstance().getLogger().severe("Failed to save sync data for player " + player.getName() + ": " + ex.getMessage());
            return null;
        });

    }
}
