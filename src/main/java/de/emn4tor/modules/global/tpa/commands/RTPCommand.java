package de.emn4tor.modules.global.tpa.commands;

import de.emn4tor.modules.global.tpa.utils.RTPInventoryHolder;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public final class RTPCommand implements BasicCommand {
    @Override
    public void execute(@NonNull CommandSourceStack commandSourceStack, String[] args) {
        if (!(commandSourceStack.getExecutor() instanceof Player player)) {
            return;
        }

        player.openInventory(this.createRTPInventory(player));
    }

    private Inventory createRTPInventory(Player player) {
        var inventory = Bukkit.createInventory(new RTPInventoryHolder(), InventoryType.CHEST);

        inventory.setItem(10, ItemStack.of(Material.GRASS_BLOCK));
        inventory.setItem(12, ItemStack.of(Material.NETHERITE_BLOCK));
        inventory.setItem(14, ItemStack.of(Material.END_STONE));

        return inventory;
    }
}
