package de.emn4tor.modules.shops.gui;

/*
 *  @author: Emn4tor
 *  @created: 24.04.2025
 */

import de.emn4tor.modules.shops.core.Shop;
import de.emn4tor.modules.shops.core.ShopItem;
import de.emn4tor.utils.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopGUIFactory {
    public static Inventory createShopInventory(Player player, Shop shop, int page) {
        Inventory inv = Bukkit.createInventory(null, 54, MiniMessage.miniMessage().deserialize("<green>" + shop.getName() + " <gray>Seite " + page));

        // Fill background
        ItemStack filler = ItemBuilder.createItem(Material.BLACK_STAINED_GLASS_PANE, "", List.of(), 0, 1);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        // Pagination
        List<ShopItem> items = shop.getItems();
        int itemsPerPage = 8;
        int start = (page - 1) * itemsPerPage;

        for (int i = 0; i < itemsPerPage; i++) {
            int index = start + i;
            if (index >= items.size()) break;
            ShopItem shopItem = items.get(index);
            inv.setItem(10 + (i % 4) * 2 + (i / 4) * 18, shopItem.toItemStack()); // Slot math for layout
        }

        inv.setItem(49, ItemBuilder.createItem(Material.BARRIER, "<red>Schließen", List.of(), 0, 0));
        if (start + itemsPerPage < items.size())
            inv.setItem(51, ItemBuilder.createItem(Material.ARROW, "<yellow>Nächste Seite", List.of(), 2, 0));
        if (page > 1)
            inv.setItem(47, ItemBuilder.createItem(Material.ARROW, "<yellow>Vorherige Seite", List.of(), 1, 0));

        return inv;
    }
}
