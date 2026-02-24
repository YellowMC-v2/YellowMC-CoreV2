package de.emn4tor.modules.global.economy.coins.commands;

/*
 * @author: Emn4tor
 * @created: 20.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import de.emn4tor.data.RedisManager;
import de.emn4tor.modules.global.economy.coins.api.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.UUID;
import org.json.JSONObject;

public class PayCommand implements CommandExecutor {

    private final EconomyManager economyManager;
    private final RedisManager redisManager;

    public PayCommand(EconomyManager economyManager, RedisManager redisManager) {
        this.economyManager = economyManager;
        this.redisManager = redisManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendRichMessage("Dieser Befehl kann nur von Spielern verwendet werden!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "economy-pay-usage", FormatService.MessageType.ERROR));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        Bukkit.getLogger().info("Sender: " + target);

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "economy-error-nan", FormatService.MessageType.ERROR));
            return true;
        }

        if (amount <= 0) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "economy-error-positive-amount", FormatService.MessageType.ERROR));
            return true;
        }

        if (target != null) {
            if (player.getName().equalsIgnoreCase(target.getName())) {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "economy-error-self", FormatService.MessageType.ERROR));
                return true;
            }

            economyManager.transferCoins(player, target, amount).thenAccept(success -> {
                if (success) {
                    player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "economy-pay-success", FormatService.MessageType.SYSTEM, Map.of("0", String.valueOf(amount), "1", target.getName())));
                    target.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(target.getUniqueId(), "economy-pay-receive", FormatService.MessageType.SYSTEM, Map.of("0", String.valueOf(amount), "1", player.getName())));
                } else {
                    player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "economy-error-insufficient-funds", FormatService.MessageType.ERROR));
                }
            });
        } else {
            Bukkit.getLogger().info("Cross-server transfer to " + args[0] + " for " + amount + " coins");
            System.out.println("Cross-server transfer to " + args[0] + " for " + amount + " coins");

            UUID fromUUID = player.getUniqueId();
            UUID toUUID;

            try {
                toUUID = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
                Bukkit.getLogger().info("Resolved UUID: " + toUUID);
            } catch (Exception e) {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "error-target-not-online", FormatService.MessageType.ERROR));
                return true;
            }

            Bukkit.getLogger().info("From UUID: " + fromUUID);

            economyManager.getCoins(fromUUID).thenAccept(balance -> {
                Bukkit.getLogger().info("Balance: " + balance);

                if (balance < amount) {
                    player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "economy-error-insufficient-funds", FormatService.MessageType.ERROR));
                    return;
                }

                Bukkit.getLogger().info("Deducting coins from " + player.getName() + " (" + fromUUID + ")");

                economyManager.removeCoins(player, amount).thenAccept(success -> {
                    Bukkit.getLogger().info("Deducted coins: " + success);

                    if (!success) {
                        Bukkit.getLogger().info("Failed to deduct coins");
                        player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "economy-error-insufficient-funds", FormatService.MessageType.ERROR));
                        return;
                    }

                    Bukkit.getLogger().info("Coins deducted successfully");

                    JSONObject payload = new JSONObject();
                    payload.put("from", fromUUID.toString());
                    payload.put("to", toUUID.toString());
                    payload.put("amount", amount);
                    payload.put("fromName", player.getName());

                    redisManager.publish("yellowmc:pay", payload.toString());

                    player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "economy-pay-success", FormatService.MessageType.SYSTEM, Map.of("0", String.valueOf(amount), "1", args[0])));
                });
            });
        }

        return true;
    }
}