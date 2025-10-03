package de.emn4tor.modules.furnitureshop;

import de.emn4tor.YellowMCCoreV2;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DebugCMD implements CommandExecutor {

    FurnitureSpawner furnitureSpawner = new FurnitureSpawner(YellowMCCoreV2.getInstance());
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        furnitureSpawner.startGenerationTaskForAllRooms();
        return true;
    }
}
