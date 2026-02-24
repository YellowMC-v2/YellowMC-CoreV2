package de.emn4tor.modules.global.economy;

/*
 *  @author: Emn4tor
 *  @created: 20.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.global.economy.coins.api.EconomyManager;
import de.emn4tor.modules.global.economy.rubies.RubyHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final EconomyManager economyManager;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public AdminCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (!sender.hasPermission("economy.admin")) {
            sender.sendMessage(mm.deserialize("<red>You don't have permission to use this command!"));
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(mm.deserialize("<red>Usage: /ecoadmin <coins|rubies> <give|take|set> <player> <amount>"));
            return true;
        }

        String currency = args[0].toLowerCase();
        String action = args[1].toLowerCase();

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            player.sendRichMessage(YellowMCCoreV2.getTranslationService().translate(player.getUniqueId(), "economy-error-nan"));
            return true;
        }

        if (amount < 0 && !action.equals("take")) {
            player.sendRichMessage(YellowMCCoreV2.getTranslationService().translate(player.getUniqueId(), "economy-error-positive-amount"));
            return true;
        }

        switch (currency) {
            case "coins" -> {
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    player.sendRichMessage(YellowMCCoreV2.getTranslationService().translate(player.getUniqueId(), "error-target-not-online"));
                    return true;
                }
                handleCoins(sender, target, action, amount);
            }
            case "rubies" -> {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                if (target == null || target.getUniqueId() == null) {
                    sender.sendMessage(mm.deserialize("<red>Player not found!"));
                    return true;
                }
                handleRubies(sender, target, target.getUniqueId(), action, amount);
            }
            default -> sender.sendMessage(mm.deserialize("<red>Invalid currency! Use coins or rubies."));
        }

        return true;
    }

    private void handleCoins(CommandSender sender, Player target, String action, int amount) {
        switch (action) {
            case "give" -> {
                economyManager.addCoins(target, amount);
                sender.sendMessage(mm.deserialize("<green>Gave <gold>" + amount + " coins <green>to <yellow>" + target.getName()));
                target.sendMessage(mm.deserialize("<green>You received <gold>" + amount + " coins <green>from an admin"));
            }
            case "take" -> {
                economyManager.removeCoins(target, Math.abs(amount)).thenAccept(success -> {
                    if (success) {
                        sender.sendMessage(mm.deserialize("<green>Took <gold>" + Math.abs(amount) + " coins <green>from <yellow>" + target.getName()));
                        target.sendMessage(mm.deserialize("<red>An admin took <gold>" + Math.abs(amount) + " coins <red>from your account"));
                    } else {
                        sender.sendMessage(mm.deserialize("<red>Player doesn't have enough coins!"));
                    }
                });
            }
            case "set" -> {
                economyManager.setCoins(target, amount);
                sender.sendMessage(mm.deserialize("<green>Set <yellow>" + target.getName() + "'s <green>balance to <gold>" + amount + " coins"));
                target.sendMessage(mm.deserialize("<yellow>Your balance was set to <gold>" + amount + " coins <yellow>by an admin"));
            }
            default -> sender.sendMessage(mm.deserialize("<red>Invalid action! Use give, take, or set."));
        }
    }

    private void handleRubies(CommandSender sender, OfflinePlayer target, UUID targetUUID, String action, int amount) {
        switch (action) {
            case "give" -> {
                RubyHandler.addRubies(targetUUID, amount);
                sender.sendMessage(mm.deserialize("<green>Gave <gold>" + amount + " rubies <green>to <yellow>" + target.getName()));
            }
            case "take" -> {
                RubyHandler.removeRubies(targetUUID, amount);
                sender.sendMessage(mm.deserialize("<green>Took <gold>" + amount + " rubies <green>from <yellow>" + target.getName()));
            }
            case "set" -> {
                RubyHandler.setRubies(targetUUID, amount);
                sender.sendMessage(mm.deserialize("<green>Set <yellow>" + target.getName() + "'s <green>rubies to <gold>" + amount));
            }
            default -> sender.sendMessage(mm.deserialize("<red>Invalid action! Use give, take, or set."));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("economy.admin")) return completions;

        if (args.length == 1) {
            completions.add("coins");
            completions.add("rubies");
        } else if (args.length == 2) {
            completions.add("give");
            completions.add("take");
            completions.add("set");
        } else if (args.length == 3) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        } else if (args.length == 4) {
            completions.add("100");
            completions.add("500");
            completions.add("1000");
        }

        return completions;
    }
}