package de.emn4tor.modules.shops.keyshop;

/*
 *  @author: Emn4tor
 *  @created: 27.05.2025
 */

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class KeyShopCommand implements CommandExecutor {
    NamespacedKey keyKey = new NamespacedKey("excellentcrates", "crate_key.id");

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        Inventory inventory = org.bukkit.Bukkit.createInventory(new KeyShopGUIHolder(), 27, MiniMessage.miniMessage().deserialize("<dark_gray>KeyShop</dark_gray>"));
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }
        inventory.setItem(11, keyItemBuilder(1016, "<dark_gray>Standard Crate Schlüssel</dark_gray>", "<gray>Ein Schlüssel für eine Crate</gray>", "10 Rubine", "normal_key"));
        inventory.setItem(15, keyItemBuilder(1026, "<yellow>Legendärer Crate Schlüssel</yellow>", "<gray>Ein Schlüssel für eine legendäre Crate</gray>", "100 Rubine", "legendrer_schlssel"));
        player.openInventory(inventory);
        return true;
    }

    private ItemStack keyItemBuilder(int customModelData, String displayName, String lore, String cost, String keyType) {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        org.bukkit.inventory.meta.ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        itemMeta.displayName(MiniMessage.miniMessage().deserialize(displayName));
        itemMeta.lore(new java.util.ArrayList<>() {{
            add(MiniMessage.miniMessage().deserialize(lore));
            add(MiniMessage.miniMessage().deserialize("<yellow>Kostet:" + " <red>" + cost + "</red></yellow>"));
            add(MiniMessage.miniMessage().deserialize("<gray>Rechtsklick auf eine Crate um einzulösen</gray>"));
        }});
        itemMeta.getPersistentDataContainer().set(keyKey, PersistentDataType.STRING , keyType);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}