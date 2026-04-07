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

public class PayCommand implements CommandExecutor {

    private final CoinService coinService;

    public PayCommand(CoinService coinService) {
        this.coinService = coinService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>Dieser Befehl kann nur von Spielern verwendet werden!");
            return true;
        }

        if (args.length != 2) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(
                    player.getUniqueId(), "economy-pay-usage", FormatService.MessageType.ERROR
            ));
            return true;
        }

        var amount = 0.0;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException exception) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(
                    player.getUniqueId(), "economy-error-nan", FormatService.MessageType.ERROR
            ));
            return true;
        }

        if (amount <= 0) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(
                    player.getUniqueId(), "economy-error-positive-amount", FormatService.MessageType.ERROR
            ));
            return true;
        }

        var targetName = args[0];
        var target = Bukkit.getOfflinePlayer(targetName);
        var targetUuid = target.getUniqueId();

        if (player.getUniqueId().equals(targetUuid)) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(
                    player.getUniqueId(), "economy-error-self", FormatService.MessageType.ERROR
            ));
            return true;
        }

        var balance = this.coinService.getCoins(player.getUniqueId());

        if (balance < amount) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(
                    player.getUniqueId(), "economy-error-insufficient-funds", FormatService.MessageType.ERROR
            ));
            return true;
        }

        this.coinService.removeCoins(player.getUniqueId(), amount);
        this.coinService.addCoins(targetUuid, amount);

        player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(
                player.getUniqueId(), "economy-pay-success", FormatService.MessageType.SYSTEM,
                Map.of("0", String.valueOf(amount), "1", targetName)
        ));

        return true;
    }
}