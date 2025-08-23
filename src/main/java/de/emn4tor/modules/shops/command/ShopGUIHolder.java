package de.emn4tor.modules.shops.command;

/*
 *  @author: Emn4tor
 *  @created: 23.06.2025
 */

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class ShopGUIHolder implements InventoryHolder {
    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
