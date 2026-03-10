package de.emn4tor.modules.global.sync.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class SyncFreezeListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (JoinSyncListener.SYNCING_PLAYERS.contains(event.getPlayer().getUniqueId())) {
            // Allow head rotation but not walking
            if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()) {
                event.setTo(event.getFrom());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (JoinSyncListener.SYNCING_PLAYERS.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (JoinSyncListener.SYNCING_PLAYERS.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (JoinSyncListener.SYNCING_PLAYERS.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}