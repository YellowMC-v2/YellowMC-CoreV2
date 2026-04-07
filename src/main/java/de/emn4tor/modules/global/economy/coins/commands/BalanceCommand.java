package de.emn4tor.modules.global.economy.coins.commands;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import de.emn4tor.modules.global.economy.coins.api.services.CoinService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BalanceCommand implements CommandExecutor {

    private final CoinService coinService;

    public BalanceCommand(CoinService coinService) {
        this.coinService = coinService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            if (args.length == 0) {
                sender.sendRichMessage("<red>Console must specify a player name!");
                return true;
            }

            var target = Bukkit.getOfflinePlayer(args[0]);
            var coins = this.coinService.getCoins(target.getUniqueId());
            sender.sendMessage("§e" + target.getName() + " hat §a" + coins + " Coins§e.");
            return true;
        }

        if (args.length == 0) {
            var coins = this.coinService.getCoins(player.getUniqueId());
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(
                    player.getUniqueId(), "economy-balance-self", FormatService.MessageType.SYSTEM, Map.of("0", String.valueOf(coins))
            ));
        } else if (args.length == 1) {
            var target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(
                        player.getUniqueId(), "error-target-not-online", FormatService.MessageType.ERROR
                ));
                return true;
            }

            var coins = this.coinService.getCoins(target.getUniqueId());
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(
                    player.getUniqueId(), "economy-balance-other", FormatService.MessageType.SYSTEM, Map.of("1", String.valueOf(coins), "0", target.getName())
            ));
        } else {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(
                    player.getUniqueId(), "economy-balance-usage", FormatService.MessageType.SYSTEM
            ));
        }
        return true;
    }
}