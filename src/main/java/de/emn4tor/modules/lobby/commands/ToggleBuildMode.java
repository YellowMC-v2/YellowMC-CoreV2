package de.emn4tor.modules.lobby.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ToggleBuildMode implements CommandExecutor {
    private static boolean buildMode;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (buildMode) {
            buildMode = false;
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Buildmode is now <red>disabled"));
        } else {
            buildMode = true;
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Buildmode is now <green>enabled"));
        }
        return true;
    }

    public static boolean getBuildMode() {
        return buildMode;
    }





}
