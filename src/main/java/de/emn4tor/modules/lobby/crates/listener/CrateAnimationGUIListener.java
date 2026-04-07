package de.emn4tor.modules.lobby.crates.listener;

import de.emn4tor.modules.lobby.crates.ui.CrateInvHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CrateAnimationGUIListener implements Listener {

    @EventHandler
    public void onCrateGUIClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof CrateInvHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCrateGUIClose(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof CrateInvHolder) {
            event.setCancelled(true); //TODO: stopping the animation and giving the reward if the player closes the inventory
        }
    }
}
