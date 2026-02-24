package de.emn4tor.modules.global.commands.languagesel;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class LanguageInvHolder implements InventoryHolder {
    private final int currentPage;

    public LanguageInvHolder(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}