package de.emn4tor.modules.smp.homes.listeners;

import de.emn4tor.modules.smp.homes.inventories.HomeInventory;
import de.emn4tor.modules.smp.homes.models.Home;
import de.emn4tor.modules.smp.homes.services.HomeService;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jspecify.annotations.NonNull;

import java.util.List;

@RequiredArgsConstructor
public class HomeInventoryListener implements Listener {
    private final HomeService homeService;

    @EventHandler
    public void onInventoryClick(@NonNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof HomeInventory.HomeHolder(List<Home> homes))) {
            return;
        }
        event.setCancelled(true);

        var player = (Player) event.getWhoClicked();
        var clickedSlot = event.getSlot();

        var homeNumber = clickedSlot + 1;

        var home = homes.stream()
                .filter(h -> h.homeNumber() == homeNumber)
                .findFirst();

        if (home.isPresent()) {
            player.teleport(home.get().toBukkitLocation());
            player.sendMessage("teleport");
            player.closeInventory();
        } else {
            this.homeService.createHome(player, homeNumber);
        }
    }
}