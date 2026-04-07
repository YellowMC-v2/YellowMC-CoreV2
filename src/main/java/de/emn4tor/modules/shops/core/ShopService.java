package de.emn4tor.modules.shops.core;

/*
 * @author: Emn4tor
 * @created: 24.04.2025
 */

import de.emn4tor.YellowMCCoreV2;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ShopService {

    private final MiniMessage mm = MiniMessage.miniMessage();

    public boolean purchase(Player player, ShopItem item) {
        var coinService = YellowMCCoreV2.getCoinService();
        var uuid = player.getUniqueId();
        double price = item.getPrice();

        if (coinService.getCoins(uuid) >= price) {
            coinService.removeCoins(uuid, price);

            ItemStack stack = new ItemStack(item.getMaterial(), item.getAmount());
            ItemMeta meta = stack.getItemMeta();

            if (meta != null) {
                if (item.getCustomModelData() != 0) {
                    meta.setCustomModelData(item.getCustomModelData());
                    meta.displayName(this.mm.deserialize(item.getDisplayName()));

                    if (item.getLore() != null && item.getLore().size() > 1) {
                        meta.lore(List.of(this.mm.deserialize(item.getLore().get(1))));
                    }
                    stack.setItemMeta(meta);
                }
            }

            player.getInventory().addItem(stack);
            player.sendMessage(this.mm.deserialize("<green>Du hast <yellow>" + item.getPrice() + "<reset> <glyph:coin> <green>ausgegeben!"));
            return true;
        } else {
            // Nicht genug Geld
            player.sendMessage(this.mm.deserialize("<red>Du hast nicht genug Geld!"));
            return false;
        }
    }
}