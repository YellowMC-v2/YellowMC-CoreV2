package de.emn4tor.modules.global.sync.listener;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.player.SyncService;
import de.emn4tor.utils.ItemStackSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JoinSyncListener implements Listener {
    private final SyncService syncService = YellowMCCoreV2.getSyncService();
    // List of players currently "frozen" while their data is syncing
    public static final Set<UUID> SYNCING_PLAYERS = new HashSet<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        SYNCING_PLAYERS.add(uuid);
        player.sendMessage("§ePlease wait, syncing your data...");

        checkLockAndLoad(player);
    }

    private void checkLockAndLoad(Player player) {
        if (!player.isOnline()) {
            SYNCING_PLAYERS.remove(player.getUniqueId());
            return;
        }

        syncService.isLocked(player.getUniqueId()).thenAccept(isLocked -> {
            if (isLocked) {
                // Wait 10 ticks and retry
                Bukkit.getScheduler().runTaskLater(YellowMCCoreV2.getInstance(),
                        () -> checkLockAndLoad(player), 10L);
                return;
            }

            syncService.fetchPlayerData(player.getUniqueId()).thenAccept(packet -> {
                if (packet == null) {
                    SYNCING_PLAYERS.remove(player.getUniqueId());
                    return;
                }

                Bukkit.getScheduler().runTask(YellowMCCoreV2.getInstance(), () -> {
                    player.setHealth(packet.getHealth());
                    player.setFoodLevel(packet.getFoodLevel());
                    player.setSaturation((float) packet.getSaturation());

                    var items = ItemStackSerializer.deserializeItems(packet.getInventoryBase64());
                    player.getInventory().setContents(items);

                    SYNCING_PLAYERS.remove(player.getUniqueId());
                    player.sendMessage("§aInventory synced!");
                });
            });
        });
    }
}