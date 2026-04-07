package de.emn4tor.modules.global.redeemables;

import de.emn4tor.modules.global.economy.coins.api.services.CoinService;
import de.emn4tor.modules.global.economy.rubies.RubyHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class RedeemListener implements Listener {

    private final CoinService coinService;
    private final NamespacedKey key = new NamespacedKey("reedemable", "amount");
    private final MiniMessage mm = MiniMessage.miniMessage();

    public RedeemListener(CoinService coinService) {
        this.coinService = coinService;
    }

    @EventHandler
    public void onRightClick(@NotNull PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT_CLICK")) return;

        var itemInHand = event.getItem();
        if (itemInHand == null || !itemInHand.hasItemMeta()) return;

        var meta = itemInHand.getItemMeta();
        if (!meta.hasCustomModelData()) return;

        var container = meta.getPersistentDataContainer();
        if (container.has(this.key, PersistentDataType.INTEGER)) {
            var amount = container.get(this.key, PersistentDataType.INTEGER);
            if (amount == null) return;

            var player = event.getPlayer();
            var cmd = meta.getCustomModelData();
            var redeemed = false;

            if (cmd == 100) {
                this.coinService.addCoins(player.getUniqueId(), amount);
                player.sendMessage(this.mm.deserialize("<green>Du hast " + amount + "<reset> <glyph:coin> <green>erhalten!"));
                redeemed = true;
            }

            else if (cmd == 101) {
                RubyHandler.addRubies(player.getUniqueId(), amount);
                player.sendMessage(this.mm.deserialize("<green>Du hast " + amount + "<reset> <glyph:ruby> <green>erhalten!"));
                redeemed = true;
            }

            if (redeemed) {
                var toRemove = itemInHand.clone();
                toRemove.setAmount(1);
                player.getInventory().removeItem(toRemove);
            }
        }
    }
}