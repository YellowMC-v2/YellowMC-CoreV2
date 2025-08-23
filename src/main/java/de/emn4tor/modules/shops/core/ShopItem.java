package de.emn4tor.modules.shops.core;

/*
 *  @author: Emn4tor
 *  @created: 24.04.2025
 *  @updated: 22.05.2025
 */

import de.emn4tor.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopItem {
    private final Material material;
    private final String displayName;
    private final List<String> lore;
    private final int price;
    private final int amount;
    private final int customModelData;

    public ShopItem(Material material, String displayName, List<String> lore, int price, int amount) {
        this(material, displayName, lore, price, amount, 0);
    }

    public ShopItem(Material material, String displayName, List<String> lore, int price, int amount, int customModelData) {
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
        this.price = price;
        this.amount = amount;
        this.customModelData = customModelData;
    }

    public Material getMaterial() { return material; }
    public String getDisplayName() { return displayName; }
    public List<String> getLore() { return lore; }
    public int getPrice() { return price; }
    public int getAmount() { return amount; }
    public int getCustomModelData() { return customModelData; }

    public ItemStack toItemStack() {
        return ItemBuilder.createItem(material, displayName, lore, customModelData, amount);
    }
}