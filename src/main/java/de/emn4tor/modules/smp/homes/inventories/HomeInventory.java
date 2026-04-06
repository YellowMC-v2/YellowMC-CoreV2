package de.emn4tor.modules.smp.homes.inventories;

import de.emn4tor.modules.smp.homes.models.Home;
import de.emn4tor.modules.smp.homes.services.HomeService;
import de.emn4tor.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class HomeInventory {
    private final HomeService homeService;

    public void openInventory(Player player) {
        var homes = this.homeService.findHomesByPlayer(player);
        var max = HomeService.MAX_HOMES;

        var inventory = Bukkit.createInventory(new HomeHolder(homes), 9, Component.text("test123"));

        for (var i = 1; i <= max; i++) {
            var slot = i - 1;
            var currentHomeNumber = i;

            var homeOptional = homes.stream()
                    .filter(h -> h.homeNumber() == currentHomeNumber)
                    .findFirst();

            if (homeOptional.isPresent()) {
                inventory.setItem(slot, homeOptional.get().toItemStack());
            } else {
                var placeholder = ItemBuilder.createItem(Material.GRASS_BLOCK, "empty", List.of(), 1, 1);

                inventory.setItem(slot, placeholder);
            }
        }

        player.openInventory(inventory);
    }

    public record HomeHolder(List<Home> homes) implements InventoryHolder {
            public HomeHolder(List<Home> homes) {
                this.homes = new ArrayList<>(homes);
            }

            @Override
            public Inventory getInventory() {
                return null;
            }
        }
}
