package de.emn4tor.modules.furnitureshop;

import de.emn4tor.YellowMCCoreV2;
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
