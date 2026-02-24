package de.emn4tor.modules.global.commands.admin;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
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
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "gamemode-self", FormatService.MessageType.SYSTEM));
                return true;
            } else {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "gamemode-invalid-gamemode", FormatService.MessageType.ERROR));
                return false;
            }
        } else if (args.length == 2) {
            if (!player.hasPermission("core.gamemode.others")) {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "gamemode-permission", FormatService.MessageType.ERROR));
                return false;
            }
            Player target = player.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "error-target-not-online", FormatService.MessageType.ERROR));
                return false;
            }
            GameMode gameMode = getGameMode(args[1]);
            if (gameMode != null) {
                target.setGameMode(gameMode);
                target.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(target.getUniqueId(), "gamemode-self", FormatService.MessageType.SYSTEM));
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "gamemode-other", FormatService.MessageType.SYSTEM));
                return true;
            } else {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "gamemode-invalid-gamemode", FormatService.MessageType.ERROR));
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
