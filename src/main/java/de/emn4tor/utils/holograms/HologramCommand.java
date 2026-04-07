package de.emn4tor.utils.holograms;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class HologramCommand implements CommandExecutor {

    private final HologramManager holoManager;
    private final MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (args.length < 2) {
                    player.sendMessage(mm.deserialize("<red>Usage: /holo create <text1|text2|...>"));
                    return true;
                }

                // Join all args after "create" and split by '|' for multi-line support
                String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                List<String> lines = Arrays.stream(input.split("\\|")).toList();

                holoManager.createHolo(player.getLocation(), lines, true);
                player.sendMessage(mm.deserialize("<green>Hologram created at your location!"));
            }

            case "delete" -> {
                // Deletes the hologram within 2 blocks of the player
                boolean removed = holoManager.removeHoloAt(player.getLocation());
                if (removed) {
                    player.sendMessage(mm.deserialize("<green>Nearest hologram deleted successfully."));
                } else {
                    player.sendMessage(mm.deserialize("<red>No hologram found nearby (within 2 blocks)."));
                }
            }

            case "near" -> {
                // Just a helper to force-update visibility for the player
                holoManager.updateForPlayer(player);
                player.sendMessage(mm.deserialize("<gray>Refreshed nearby holograms."));
            }

            default -> sendHelp(player);
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(mm.deserialize("<gold><bold>Hologram Helper Commands:"));
        player.sendMessage(mm.deserialize("<yellow>/holo create <text|line2> <gray>- Create at your feet (use | for lines)"));
        player.sendMessage(mm.deserialize("<yellow>/holo delete <gray>- Delete the hologram you are standing on"));
        player.sendMessage(mm.deserialize("<yellow>/holo near <gray>- Refresh nearby holograms"));
    }
}