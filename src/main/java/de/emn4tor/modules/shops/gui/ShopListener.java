package de.emn4tor.modules.shops.gui;

/*
 *  @author: Emn4tor
 *  @created: 24.04.2025
 */

import de.emn4tor.modules.shops.core.Shop;
import de.emn4tor.modules.shops.core.ShopService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ShopListener implements Listener {
    private final Map<String, Shop> shops;
    private final ShopService shopService;

    public ShopListener(Map<String, Shop> shops, ShopService shopService) {
        this.shops = shops;
        this.shopService = shopService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().title().toString();

        for (Shop shop : shops.values()) {
            if (title.contains(shop.getName())) {
                event.setCancelled(true);
                ItemStack clicked = event.getCurrentItem();
                if (clicked == null || clicked.getType() == Material.AIR) return;

                if (clicked.getType() == Material.BARRIER) {
                    player.closeInventory();
                    return;
                }

                if (clicked.getType() == Material.ARROW && clicked.hasItemMeta()) {
                    int modelData = clicked.getItemMeta().hasCustomModelData()
                            ? clicked.getItemMeta().getCustomModelData() : 0;

                    int currentPage = title.contains("2") ? 2 : 1;
                    int newPage = modelData == 1 ? currentPage - 1 : modelData == 2 ? currentPage + 1 : currentPage;
                    player.openInventory(ShopGUIFactory.createShopInventory(player, shop, newPage));
                    return;
                }

                shop.getItems().stream()
                        .filter(item -> item.getMaterial() == clicked.getType())
                        .findFirst()
                        .ifPresent(item -> shopService.purchase(player, item));
            }
        }
    }
}
