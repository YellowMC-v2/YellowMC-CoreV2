package de.emn4tor.modules.economy.coins.commands;

/*
 *  @author: Emn4tor
 *  @created: 20.08.2025
 */

import de.emn4tor.modules.economy.coins.api.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    private final EconomyManager economyManager;

    public BalanceCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendRichMessage("<red>Console must specify a player name!");
            return true;
        }

        if (args.length == 0) {
            Player player = (Player) sender;
            economyManager.getCoins(player.getUniqueId()).thenAccept(coins -> {
                player.sendRichMessage("<green>Du hast <gold>" + coins + " <gray>coins");
            });
        } else if (args.length == 1) {
            if (!sender.hasPermission("economy.balance.others")) {
                sender.sendRichMessage("<red>You don't have permission to check other players' balances!");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendRichMessage("<red>Player not found!");
                return true;
            }

            economyManager.getCoins(target.getUniqueId()).thenAccept(coins -> {
                sender.sendRichMessage("<yellow>" + target.getName() + "<green>'s balance: <gold>" + coins + " <gray>coins");
            });
        } else {
            sender.sendRichMessage("<red>Usage: /balance [player]");
        }

        return true;
    }
}
