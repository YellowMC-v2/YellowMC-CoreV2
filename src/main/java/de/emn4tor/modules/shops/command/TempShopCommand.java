package de.emn4tor.modules.shops.command;

/*
 *  @author: Emn4tor
 *  @created: 22.05.2025
 */

import de.emn4tor.modules.shops.core.TempShopManager;
import de.emn4tor.modules.shops.gui.ShopGUIFactory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TempShopCommand implements CommandExecutor {
    private final TempShopManager tempShopManager;

    public TempShopCommand(TempShopManager tempShopManager) {
        this.tempShopManager = tempShopManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            player.openInventory(ShopGUIFactory.createShopInventory(player, tempShopManager.getTempShop(), 1));
        }
        return true;
    }
}