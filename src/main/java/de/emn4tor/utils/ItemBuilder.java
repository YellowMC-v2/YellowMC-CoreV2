package de.emn4tor.utils;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static ItemStack createItem(Material type, String name, List<String> lore, int customModelData, int amount) {
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        Component displayName = miniMessage.deserialize(name);
        List<Component> loreComponents = lore.stream()
                .map(miniMessage::deserialize)
                .collect(Collectors.toList());

        meta.displayName(displayName);
        meta.lore(loreComponents);
        meta.setCustomModelData(customModelData);

        if (amount != 0) {
            item.setAmount(amount);
        }
        item.setItemMeta(meta);
        return item;
    }

}