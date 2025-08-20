package de.emn4tor.modules.economy;

/*
 *  @author: Emn4tor
 *  @created: 20.08.2025
 */

import de.emn4tor.modules.economy.coins.api.EconomyManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final EconomyManager economyManager;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public AdminCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("economy.admin")) {
            sender.sendMessage(mm.deserialize("<red>You don't have permission to use this command!"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(mm.deserialize("<red>Usage: /ecoadmin <give|take|set> <player> <amount>"));
            return true;
        }

        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage(mm.deserialize("<red>Player not found!"));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(mm.deserialize("<red>Amount must be a number!"));
            return true;
        }

        if (amount < 0 && !action.equals("take")) {
            sender.sendMessage(mm.deserialize("<red>Amount must be positive!"));
            return true;
        }

        switch (action) {
            case "give":
                economyManager.addCoins(target, amount);
                sender.sendMessage(mm.deserialize("<green>Gave <gold>" + amount + " coins <green>to <yellow>" + target.getName()));
                target.sendMessage(mm.deserialize("<green>You received <gold>" + amount + " coins <green>from an admin"));
                break;

            case "take":
                economyManager.removeCoins(target, Math.abs(amount)).thenAccept(success -> {
                    if (success) {
                        sender.sendMessage(mm.deserialize("<green>Took <gold>" + Math.abs(amount) + " coins <green>from <yellow>" + target.getName()));
                        target.sendMessage(mm.deserialize("<red>An admin took <gold>" + Math.abs(amount) + " coins <red>from your account"));
                    } else {
                        sender.sendMessage(mm.deserialize("<red>Player doesn't have enough coins!"));
                    }
                });
                break;

            case "set":
                economyManager.setCoins(target, amount);
                sender.sendMessage(mm.deserialize("<green>Set <yellow>" + target.getName() + "'s <green>balance to <gold>" + amount + " coins"));
                target.sendMessage(mm.deserialize("<yellow>Your balance was set to <gold>" + amount + " coins <yellow>by an admin"));
                break;

            default:
                sender.sendMessage(mm.deserialize("<red>Invalid action! Use give, take, or set."));
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("economy.admin")) return completions;

        if (args.length == 1) {
            completions.add("give");
            completions.add("take");
            completions.add("set");
        } else if (args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        } else if (args.length == 3) {
            completions.add("100");
            completions.add("500");
            completions.add("1000");
        }

        return completions;
    }
}
