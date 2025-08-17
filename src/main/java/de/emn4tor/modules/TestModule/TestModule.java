package de.emn4tor.modules.TestModule;

/*
 *  @author: Emn4tor
 *  @created: 17.08.2025
 */

import de.emn4tor.Module;
import de.emn4tor.YellowMCCoreV2;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TestModule implements Module, CommandExecutor {

    @Override
    public String getName() {
        return "CommandsModule";
    }

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        plugin.getCommand("hello").setExecutor(this);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage("Hello from CommandsModule!");
        return true;
    }
}