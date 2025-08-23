package de.emn4tor.modules.shops.core;

/*
 *  @author: Emn4tor
 *  @created: 24.04.2025
 */

import de.emn4tor.modules.economy.coins.api.EconomyHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ShopService {
    public boolean purchase(Player player, ShopItem item) {
        if (EconomyHandler.purchaseItem(player, item.getPrice())) {
            ItemStack stack = new ItemStack(item.getMaterial(), item.getAmount());
            ItemMeta meta = stack.getItemMeta();
            player.sendRichMessage(meta.toString());
            if (item.getCustomModelData() != 0) {
                player.sendRichMessage("<green>Das item hat eine CustomModelData von <yellow>" + item.getCustomModelData() + "<green>!");
                meta.setCustomModelData(item.getCustomModelData());
                meta.displayName(MiniMessage.miniMessage().deserialize(item.getDisplayName()));
                meta.lore(List.of(MiniMessage.miniMessage().deserialize(item.getLore().get(1))));
                stack.setItemMeta(meta);
            }
            player.getInventory().addItem(stack);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Du hast " + item.getPrice() + "<reset>ꑻ <green>ausgegeben!"));
            return true;
        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Du hast nicht genug Geld!"));
            return false;
        }
    }
}
