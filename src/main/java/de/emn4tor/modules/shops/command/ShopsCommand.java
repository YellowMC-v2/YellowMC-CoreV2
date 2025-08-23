package de.emn4tor.modules.shops.command;

/*
 *  @author: Emn4tor
 *  @created: 23.06.2025
 */

import de.emn4tor.utils.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShopsCommand implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Inventory inventory = player.getServer().createInventory(new ShopGUIHolder(), 27, "Shopübersicht");
        for (int i = 0; i < 26; i++) {
            inventory.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }
        inventory.setItem(10, ItemBuilder.createItem(Material.COOKED_BEEF, "Essen", List.of("<gray>Hier kannst du Essen kaufen."), 0, 1));
        inventory.setItem(16, ItemBuilder.createItem(Material.OAK_LOG, "Holz", List.of("<gray>Hier kannst du Holz kaufen."), 0, 1));
        inventory.setItem(12, ItemBuilder.createItem(Material.PAPER, "Schlüssel", List.of("<gray>Hier kannst du Schlüssel kaufen, die du am Spawn nutzen kannst um Kisten zu öffnen."), 1022, 1));
        inventory.setItem(14, ItemBuilder.createItem(Material.POPPY, "Pflanzen", List.of("<gray>Hier kannst du Pflanzen kaufen."), 0, 1));
        inventory.setItem(22, ItemBuilder.createItem(Material.BARRIER, "Schließen", List.of("<gray>Schließt das Shopmenü."), 0, 1));
        player.openInventory(inventory);
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof ShopGUIHolder)) return;

        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        switch (clickedItem.getType()) {
            case COOKED_BEEF -> player.performCommand("foodshop");
            case OAK_LOG -> player.performCommand("woodshop");
            case PAPER -> player.performCommand("keyshop");
            case POPPY -> player.performCommand("plantshop");
            case BARRIER -> player.closeInventory();
            default -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Dieser Shop ist noch nicht implementiert."));
        }
    }
}
