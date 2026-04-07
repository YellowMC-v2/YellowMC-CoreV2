package de.emn4tor.modules.lobby.crates.commands;

import de.emn4tor.modules.lobby.crates.CratesModule;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class CratesCommand implements CommandExecutor {
    private final CratesModule cratesModule;


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length == 0) {
            sender.sendMessage("Usage: /crates <reload|givekey>");
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            cratesModule.reload();
            sender.sendMessage("Crates configuration reloaded.");
        }
        return true;
    }
}
