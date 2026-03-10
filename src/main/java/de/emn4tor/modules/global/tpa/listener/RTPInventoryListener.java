package de.emn4tor.modules.global.tpa.listener;

import de.emn4tor.modules.global.tpa.api.RandomTeleportAPI;
import de.emn4tor.modules.global.tpa.utils.RTPInventoryHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class RTPInventoryListener implements Listener {
    private final RandomTeleportAPI randomTeleportAPI;

    public RTPInventoryListener(RandomTeleportAPI randomTeleportAPI) {
        this.randomTeleportAPI = randomTeleportAPI;
    }

    @EventHandler
    public void handle(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof RTPInventoryHolder)) {
            return;
        }

        event.setCancelled(true);

        var player = (Player) event.getWhoClicked();
        var clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }

        switch (event.getRawSlot()) {
            case 10 -> this.randomTeleportAPI.rtpPlayer(player, "world");
            case 12 -> this.randomTeleportAPI.rtpPlayer(player, "world_nether");
            case 14 -> this.randomTeleportAPI.rtpPlayer(player, "world_the_end");
        }

        player.closeInventory();
    }
}
