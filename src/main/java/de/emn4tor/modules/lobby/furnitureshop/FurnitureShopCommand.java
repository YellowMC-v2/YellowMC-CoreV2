package de.emn4tor.modules.lobby.furnitureshop;

import de.emn4tor.YellowMCCoreV2;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class FurnitureShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendRichMessage("<red>Use /furnitureshop <setprice|refresh|setroomcount>");
            return true;
        }

        FileConfiguration config = YellowMCCoreV2.getInstance().getConfig();

        // Refresh
        if (args.length == 1 && args[0].equalsIgnoreCase("refresh")) {
            if (FurnitureShopModule.getFurnitureSpawner() != null) {
                FurnitureShopModule.getFurnitureSpawner().startGenerationTaskForAllRooms();
                sender.sendRichMessage("<green>All furniture shops have been refreshed.");
            } else {
                sender.sendRichMessage("<red>Furniture spawner is not initialized.");
            }
            return true;
        }

        // Set price
        if (args.length == 3 && args[0].equalsIgnoreCase("setprice")) {
            try {
                String furnitureId = args[1];
                int price = Integer.parseInt(args[2]);
                FurnitureShopManager.setPrice(furnitureId, price);
                sender.sendRichMessage("<green>Price for " + furnitureId + " set to " + price);
            } catch (NumberFormatException e) {
                sender.sendRichMessage("<red>Price must be a valid number.");
            }
            return true;
        }

        // Set room count
        if (args.length == 3 && args[0].equalsIgnoreCase("setroomcount")) {
            try {
                int room = Integer.parseInt(args[1]);
                int count = Integer.parseInt(args[2]);

                String basePath = "furniture.room_" + room + ".roomCount";
                config.set(basePath, count);
                YellowMCCoreV2.getInstance().saveConfig();

                sender.sendRichMessage("<green>Room " + room + " count set to " + count);
            } catch (NumberFormatException e) {
                sender.sendRichMessage("<red>Room number and count must be valid numbers.");
            }
            return true;
        }

        sender.sendRichMessage("<red>Unknown subcommand. Use /furnitureshop <setprice|refresh|setroomcount>");
        return true;
    }
}
