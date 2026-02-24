package de.emn4tor.modules.global.commands.basic;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack mainHand = player.getInventory().getItemInMainHand();

        player.getInventory().setHelmet(mainHand);
        player.getInventory().setItemInMainHand(helmet);
        return true;
    }
}
