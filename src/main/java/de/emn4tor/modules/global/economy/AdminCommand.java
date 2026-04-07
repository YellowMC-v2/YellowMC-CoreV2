package de.emn4tor.modules.global.economy;

import de.emn4tor.modules.global.economy.coins.api.services.CoinService;
import de.emn4tor.modules.global.economy.rubies.RubyHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final CoinService coinService;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public AdminCommand(CoinService coinService) {
        this.coinService = coinService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("economy.admin")) {
            sender.sendMessage(this.mm.deserialize("<red>You don't have permission to use this command!"));
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(this.mm.deserialize("<red>Usage: /ecoadmin <coins|rubies> <give|take|set> <player> <amount>"));
            return true;
        }

        var currency = args[0].toLowerCase();
        var action = args[1].toLowerCase();
        var targetName = args[2];

        double amount;
        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException exception) {
            sender.sendMessage(this.mm.deserialize("<red>Invalid number amount!"));
            return true;
        }

        var target = Bukkit.getOfflinePlayer(targetName);
        var uuid = target.getUniqueId();

        switch (currency) {
            case "coins" -> this.handleCoins(sender, target.getName(), uuid, action, amount);
            case "rubies" -> this.handleRubies(sender, target.getName(), uuid, action, (int) amount);
            default -> sender.sendMessage(this.mm.deserialize("<red>Invalid currency! Use coins or rubies."));
        }

        return true;
    }

    private void handleCoins(CommandSender sender, String name, java.util.UUID uuid, String action, double amount) {
        switch (action) {
            case "give" -> {
                this.coinService.addCoins(uuid, amount);
                sender.sendMessage(this.mm.deserialize("<green>Gave <gold>" + amount + " coins <green>to <yellow>" + name));
            }
            case "take" -> {
                this.coinService.removeCoins(uuid, amount);
                sender.sendMessage(this.mm.deserialize("<green>Took <gold>" + amount + " coins <green>from <yellow>" + name));
            }
            case "set" -> {
                this.coinService.setCoins(uuid, amount);
                sender.sendMessage(this.mm.deserialize("<green>Set <yellow>" + name + "'s <green>balance to <gold>" + amount + " coins"));
            }
            default -> sender.sendMessage(this.mm.deserialize("<red>Invalid action! Use give, take, or set."));
        }
    }

    private void handleRubies(CommandSender sender, String name, java.util.UUID uuid, String action, int amount) {
        switch (action) {
            case "give" -> {
                RubyHandler.addRubies(uuid, amount);
                sender.sendMessage(this.mm.deserialize("<green>Gave <gold>" + amount + " rubies <green>to <yellow>" + name));
            }
            case "take" -> {
                RubyHandler.removeRubies(uuid, amount);
                sender.sendMessage(this.mm.deserialize("<green>Took <gold>" + amount + " rubies <green>from <yellow>" + name));
            }
            case "set" -> {
                RubyHandler.setRubies(uuid, amount);
                sender.sendMessage(this.mm.deserialize("<green>Set <yellow>" + name + "'s <green>rubies to <gold>" + amount));
            }
            default -> sender.sendMessage(this.mm.deserialize("<red>Invalid action! Use give, take, or set."));
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        var completions = new ArrayList<String>();
        if (!sender.hasPermission("economy.admin")) return completions;

        if (args.length == 1) {
            completions.add("coins");
            completions.add("rubies");
        } else if (args.length == 2) {
            completions.add("give");
            completions.add("take");
            completions.add("set");
        } else if (args.length == 3) {
            for (var p : Bukkit.getOnlinePlayers()) completions.add(p.getName());
        } else if (args.length == 4) {
            completions.addAll(List.of("100", "500", "1000"));
        }

        return completions;
    }
}