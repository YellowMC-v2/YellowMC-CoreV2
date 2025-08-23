package de.emn4tor.modules.redeemables;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.modules.economy.coins.api.EconomyHandler;
import de.emn4tor.modules.economy.rubies.RubyHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class RedeemListener implements Listener {
    NamespacedKey key = new NamespacedKey("reedemable", "amount");

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){
        if (!event.getAction().toString().contains("RIGHT_CLICK")) {return;}
        if (event.getItem() == null || !event.getItem().hasItemMeta()) {return;}
        ItemMeta meta = event.getItem().getItemMeta();
        if (!meta.hasCustomModelData()) {return;}
        int cmd = meta.getCustomModelData();
        if (meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            int amount = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
            Player player = event.getPlayer();
            if (cmd == 100) {
                EconomyHandler.addCoins(player, amount);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Du hast " + amount + "<reset> ꑻ <green>erhalten!"));
                ItemStack item = event.getItem().clone();
                item.setAmount(1);
                player.getInventory().removeItem(item);
            }
            if (cmd == 101) {
                RubyHandler.addRubies(player.getUniqueId(), amount);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Du hast " + amount + "<reset> ꑺ <green>erhalten!"));
                ItemStack item = event.getItem().clone();
                item.setAmount(1);
                player.getInventory().removeItem(item);
            }
        }

    }
}
