package de.emn4tor.modules.commands.admin;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GamemodeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if(args.length == 1) {
            GameMode gameMode = getGameMode(args[0]);
            if (gameMode != null) {
                player.setGameMode(gameMode);
                player.sendMessage("§aDein Spielmodus wurde auf §6" + gameMode.name() + " §agesetzt.");
                return true;
            } else {
                player.sendMessage("§cUngültiger Spielmodus. Verfügbare Modi: 0, 1, 2, 3, s, c, a, sp");
                return false;
            }
        } else if (args.length == 2) {
            if (!player.hasPermission("core.gamemode.others")) {
                player.sendMessage("§cDu hast keine Berechtigung, den Spielmodus anderer Spieler zu ändern.");
                return false;
            }
            Player target = player.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cDer Spieler ist nicht online.");
                return false;
            }
            GameMode gameMode = getGameMode(args[1]);
            if (gameMode != null) {
                target.setGameMode(gameMode);
                target.sendMessage("§aDein Spielmodus wurde auf §6" + gameMode.name() + " §agesetzt.");
                player.sendMessage("§aDer Spielmodus von §6" + target.getName() + " §awurde auf §6" + gameMode.name() + " §agesetzt.");
                return true;
            } else {
                player.sendMessage("§cUngültiger Spielmodus. Verfügbare Modi: 0, 1, 2, 3, s, c, a, sp");
                return false;
            }
        }
        return false;
    }

    private GameMode getGameMode(String mode) {
        return switch (mode.toLowerCase()) {
            case "0", "s", "survival" -> GameMode.SURVIVAL;
            case "1", "c", "creative" -> GameMode.CREATIVE;
            case "2", "a", "adventure" -> GameMode.ADVENTURE;
            case "3", "sp", "spectator" -> GameMode.SPECTATOR;
            default -> null;
        };
    }
}
