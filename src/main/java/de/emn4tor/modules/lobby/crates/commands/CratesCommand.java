package de.emn4tor.modules.lobby.crates.commands;

import de.emn4tor.modules.lobby.crates.CratesModule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CratesCommand implements CommandExecutor, TabCompleter {

    private final CratesModule module;

    public CratesCommand(CratesModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("crates.admin")) {
            sender.sendRichMessage("<red>You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give" -> handleGive(sender, args);
            case "info" -> handleInfo(sender, args);
            case "reload" -> handleReload(sender);
            default -> sendHelp(sender);
        }

        return true;
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendRichMessage("<red>Usage: /crates give <player> <crateId> <amount>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendRichMessage("<red>Player not found.");
            return;
        }

        String crateId = args[2].toLowerCase();
        if (!module.getRegistry().getCrate(crateId).isPresent()) {
            sender.sendRichMessage("<red>Crate '" + crateId + "' does not exist.");
            return;
        }

        try {
            int amount = Integer.parseInt(args[3]);
            module.getKeyRepository().giveKeys(target.getUniqueId(), crateId, amount);

            sender.sendRichMessage("<green>Gave <yellow>" + amount + "x " + crateId + " <green>keys to <white>" + target.getName());
            target.sendRichMessage("<gray>You received <yellow>" + amount + "x " + crateId + " <gray>keys!");
        } catch (NumberFormatException e) {
            sender.sendRichMessage("<red>Amount must be a number.");
        }
    }

    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendRichMessage("<red>Usage: /crates info <player> <crateId>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendRichMessage("<red>Player not found.");
            return;
        }

        String crateId = args[2].toLowerCase();
        int keys = module.getKeyRepository().getKeys(target.getUniqueId(), crateId);
        sender.sendRichMessage("<yellow>" + target.getName() + " <gray>has <yellow>" + keys + " <gray>keys for <white>" + crateId);
    }

    private void handleReload(CommandSender sender) {
        module.reload();
        sender.sendRichMessage("<green>Crate configurations reloaded!");
    }

    private void sendHelp(CommandSender sender) {
        sender.sendRichMessage("<dark_gray>--- <yellow>Crates Help <dark_gray>---");
        sender.sendRichMessage("<yellow>/crates give <player> <id> <amt> <gray>- Give keys");
        sender.sendRichMessage("<yellow>/crates info <player> <id> <gray>- Check keys");
        sender.sendRichMessage("<yellow>/crates reload <gray>- Reload configs");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(List.of("give", "info", "reload"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("info")) {
                return null;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("info")) {
                completions.addAll(module.getRegistry().getAllCrates().keySet());
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            completions.addAll(List.of("1", "5", "10"));
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(lastArg))
                .collect(Collectors.toList());
    }
}