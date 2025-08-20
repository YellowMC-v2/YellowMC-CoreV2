package de.emn4tor.modules.economy.coins.commands;

/*
 *  @author: Emn4tor
 *  @created: 20.08.2025
 */

import de.emn4tor.data.RedisManager;
import de.emn4tor.modules.economy.coins.api.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            sender.sendRichMessage("<red>Dieser Befehl kann nur von Spielern verwendet werden!");
            return true;
        }

        if (args.length != 2) {
            sender.sendRichMessage("<red>Nutze: /pay <spieler> <betrag>");
            return true;
        }

        Player senderPlayer = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        Bukkit.getLogger().info("Sender: " + target);
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            senderPlayer.sendRichMessage("<red>Amount must be a number!");
            return true;
        }

        if (amount <= 0) {
            senderPlayer.sendRichMessage("<red>Amount must be positive!");
            return true;
        }

        if (target != null) {
            if (senderPlayer.getName().equalsIgnoreCase(target.getName())) {
                senderPlayer.sendRichMessage("<red>You cannot pay yourself!");
                return true;
            }
            economyManager.transferCoins(senderPlayer, target, amount).thenAccept(success -> {
                if (success) {
                    senderPlayer.sendRichMessage("<green>You paid <gold>" + amount + " <gray>coins to <yellow>" + target.getName());
                    target.sendRichMessage("<green>You received <gold>" + amount + " <gray>coins from <yellow>" + senderPlayer.getName());
                } else {
                    senderPlayer.sendRichMessage("<red>You don't have enough coins!");
                }
            });
        } else {
            Bukkit.getLogger().info("Cross-server transfer to " + args[0] + " for " + amount + " coins");
            System.out.println("Cross-server transfer to " + args[0] + " for " + amount + " coins");

            UUID fromUUID = senderPlayer.getUniqueId();
            UUID toUUID;

            try {
                toUUID = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
                Bukkit.getLogger().info("Resolved UUID: " + toUUID);
            } catch (Exception e) {
                senderPlayer.sendRichMessage("<red>Spieler nicht gefunden!");
                return true;
            }
            Bukkit.getLogger().info("From UUID: " + fromUUID);

            economyManager.getCoins(fromUUID).thenAccept(balance -> {
                Bukkit.getLogger().info("Balance: " + balance);
                if (balance < amount) {
                    senderPlayer.sendRichMessage("<red>You don't have enough coins!");
                    return;
                }
                Bukkit.getLogger().info("Deducting coins from " + senderPlayer.getName() + " (" + fromUUID + ")");

                economyManager.removeCoins(senderPlayer, amount).thenAccept(success -> {
                    Bukkit.getLogger().info("Deducted coins: " + success);
                    if (!success) {
                        Bukkit.getLogger().info("Failed to deduct coins");
                        senderPlayer.sendRichMessage("<red>You don't have enough coins!");
                        return;
                    }
                    Bukkit.getLogger().info("Coins deducted successfully");

                    JSONObject payload = new JSONObject();
                    payload.put("from", fromUUID.toString());
                    payload.put("to", toUUID.toString());
                    payload.put("amount", amount);
                    payload.put("fromName", senderPlayer.getName());

                    redisManager.publish("yellowmc:pay", payload.toString());
                    senderPlayer.sendRichMessage("<green>You paid <gold>" + amount + " <gray>coins to <yellow>" + args[0] + " <dark_gray>(cross-server)");
                });
            });
        }

        return true;
    }
}
