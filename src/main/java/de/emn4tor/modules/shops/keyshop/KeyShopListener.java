package de.emn4tor.modules.shops.keyshop;

/*
 *  @author: Emn4tor
 *  @created: 27.05.2025
 */

import de.emn4tor.modules.economy.rubies.RubyHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class KeyShopListener implements Listener {
    @EventHandler
    public void invClick(InventoryClickEvent event) {
        String title = event.getView().title().toString();

        if (!(event.getInventory().getHolder() instanceof KeyShopGUIHolder holder)) return;
        event.setCancelled(true);
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§cDein Inventar ist voll!");
            player.closeInventory();
            return;
        }
        if (slot == 11) {
            handleKeyPurchase(player, event.getInventory(), 13, 10);
        }
        else if (slot == 15){
            handleKeyPurchase(player, event.getInventory(), 14, 100);
        }
    }

    public void handleKeyPurchase(Player player, Inventory inventory, int slot, int price) {
        RubyHandler.getRubiesAsync(player.getUniqueId()).thenAccept(rubies -> {
            if (rubies >= price) {
                RubyHandler.removeRubies(player.getUniqueId(), price);
                player.getInventory().addItem(inventory.getItem(slot));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Du hast erfolgreich einen Schlüssel gekauft!"));
            } else {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Du hast nicht genug Rubine! Du benötigst " + price + " Rubine, um einen Schlüssel zu kaufen."));
                player.closeInventory();
            }
        });
    }





}
