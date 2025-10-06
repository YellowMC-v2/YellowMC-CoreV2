package de.emn4tor.modules.furnitureshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class FurnitureShopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sender.sendRichMessage("<red>Use /furnitureshop <setprice|refresh>");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("refresh")) {
            if (FurnitureShopModule.getFurnitureSpawner() != null) {
                FurnitureShopModule.getFurnitureSpawner().startGenerationTaskForAllRooms();
                sender.sendRichMessage("<green>All furniture shops have been refreshed.");
            } else {
                sender.sendRichMessage("<red>Furniture spawner is not initialized.");
            }
            return true;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("setprice")) {
            try {
                String furnitureId = args[1];
                int price = Integer.parseInt(args[2]);
                FurnitureShopManager.setPrice(furnitureId, price);
            } catch (NumberFormatException e) {
                sender.sendRichMessage("<red>Price must be a valid number.");
            }
            return true;
        }

        sender.sendRichMessage("<red>Unknown subcommand. Use /furnitureshop <setprice|refresh>");
        return true;
    }
}
