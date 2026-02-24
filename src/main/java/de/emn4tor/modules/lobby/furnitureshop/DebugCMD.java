package de.emn4tor.modules.lobby.furnitureshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DebugCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        assert FurnitureShopModule.getFurnitureSpawner() != null;
        FurnitureShopModule.getFurnitureSpawner().startGenerationTaskForAllRooms();
        return true;
    }
}
