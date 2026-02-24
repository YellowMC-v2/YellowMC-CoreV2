package de.emn4tor.modules.lobby.lobbyessentials.listeners;

import de.emn4tor.modules.lobby.lobbyessentials.commands.ToggleBuildMode;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CanceledEvents implements Listener {

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        cancelEvent(event);
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        cancelEvent(event);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        cancelEvent(event);
    }

    @EventHandler
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent event) {
        cancelEvent(event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        cancelEvent(event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        cancelEvent(event);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) { cancelEvent(event); }


    public static void cancelEvent(Event event) {
        if (!ToggleBuildMode.getBuildMode() && event instanceof Cancellable cancellable) {
            cancellable.setCancelled(true);
        }


    }


}
