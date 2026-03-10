package de.emn4tor.modules.global.sync;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.models.PlayerSyncPacket;
import de.emn4tor.utils.ItemStackSerializer;
import org.bukkit.entity.Player;

public class SyncPacketUtil {
    public static PlayerSyncPacket createSyncPacket(Player player) {
        String inventoryBase64 = ItemStackSerializer.serializeItems(player.getInventory().getContents());

        return new PlayerSyncPacket(
                player.getUniqueId(),
                inventoryBase64,
                player.getHealth(),
                player.getFoodLevel(),
                player.getSaturation(),
                System.currentTimeMillis()
        );
    }
}