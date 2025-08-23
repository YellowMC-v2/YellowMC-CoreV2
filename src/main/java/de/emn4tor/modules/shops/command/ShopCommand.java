package de.emn4tor.modules.shops.command;

/*
 *  @author: Emn4tor
 *  @created: 24.04.2025
 */

import de.emn4tor.modules.shops.core.Shop;
import de.emn4tor.modules.shops.gui.ShopGUIFactory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopCommand implements CommandExecutor {
    private final Shop shop;

    public ShopCommand(Shop shop) {
        this.shop = shop;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            player.openInventory(ShopGUIFactory.createShopInventory(player, shop, 1));
        }
        return true;
    }
}
