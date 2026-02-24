package de.emn4tor.modules.global.commands.workstations;

/*
 *  @author: Emn4tor
 *  @created: 12.04.2025
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StoneCutterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof org.bukkit.entity.Player player) {
            player.openStonecutter(null, true);
        } else {
            commandSender.sendMessage("This command can only be used by players.");
        }
        return true;
    }
}
