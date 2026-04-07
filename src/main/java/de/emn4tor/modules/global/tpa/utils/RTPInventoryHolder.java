package de.emn4tor.modules.global.tpa.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public record RTPInventoryHolder() implements InventoryHolder {
    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
