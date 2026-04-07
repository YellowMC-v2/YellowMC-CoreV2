package de.emn4tor.modules.lobby.crates.listener;

import de.emn4tor.modules.lobby.crates.registry.CrateManager;
import de.emn4tor.modules.lobby.crates.ui.CrateInvHolder;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

@AllArgsConstructor
public class CrateAnimationGUIListener implements Listener {
    private final CrateManager crateManager;


    @EventHandler
    public void onCrateGUIClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof CrateInvHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCrateGUIClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof CrateInvHolder) {
            Player player = (Player) event.getPlayer();
            if (event.getView().getTitle().contains("Opening")) {
                crateManager.handleGUIClose(player);
            }
        }
    }
}
