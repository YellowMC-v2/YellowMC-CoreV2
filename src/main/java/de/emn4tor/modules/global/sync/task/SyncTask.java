package de.emn4tor.modules.global.sync.task;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.player.SyncService;
import de.emn4tor.modules.global.sync.SyncPacketUtil;
import org.bukkit.Bukkit;

public class SyncTask {
    private static final long INTERVAL = 20L * 60 * 5; // 5 minutes

    public static void startSyncTask() {
        Bukkit.getScheduler().runTaskTimer(YellowMCCoreV2.getInstance(), () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                YellowMCCoreV2.getSyncService().savePlayerDataToDB(
                        player.getUniqueId(),
                        SyncPacketUtil.createSyncPacket(player)
                );
            });
        }, INTERVAL, INTERVAL);
    }
}
