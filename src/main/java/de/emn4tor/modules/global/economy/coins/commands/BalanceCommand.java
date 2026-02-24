package de.emn4tor.modules.global.economy.coins.commands;

/*
 *  @author: Emn4tor
 *  @created: 20.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import de.emn4tor.modules.global.economy.coins.api.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

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
        Player player = (Player) sender;

        if (args.length == 0) {
            economyManager.getCoins(player.getUniqueId()).thenAccept(coins -> {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "economy-balance-self", FormatService.MessageType.ERROR, Map.of("0", String.valueOf(coins))));
            });
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "error-target-not-online",FormatService.MessageType.ERROR));
                return true;
            }

            economyManager.getCoins(target.getUniqueId()).thenAccept(coins -> {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "economy-balance-other",FormatService.MessageType.SYSTEM , Map.of("1", String.valueOf(coins), "0", target.getName())));
            });
        } else {
            sender.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "economy-balance-usage",FormatService.MessageType.SYSTEM));
        }
        return true;
    }
}
