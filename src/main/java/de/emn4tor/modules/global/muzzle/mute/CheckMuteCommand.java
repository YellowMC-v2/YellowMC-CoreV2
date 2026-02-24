package de.emn4tor.modules.global.muzzle.mute;

/*
 *  @author: Emn4tor
 *  @created: 04.05.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class CheckMuteCommand implements CommandExecutor {
    private final MuteManager muteManager;

    public CheckMuteCommand(YellowMCCoreV2 plugin) {
        this.muteManager = new MuteManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) return false;
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "error-target-not-online", FormatService.MessageType.ERROR));
            return true;
        }

        UUID uuid = target.getUniqueId();
        if (!muteManager.isMuted(uuid)) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "mute-not-found", FormatService.MessageType.ERROR, Map.of("0", target.getName())));
            return true;
        }

        long expiresAt = muteManager.getExpiresAt(uuid);
        if (expiresAt == 0) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "mute-permanent", FormatService.MessageType.ERROR, Map.of("0", target.getName())));
            sender.sendMessage("§e" + target.getName() + " is permanently muted.");
        } else {
            long remainingMs = expiresAt - System.currentTimeMillis();
            if (remainingMs < 0) remainingMs = 0;
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "mute-temporary", FormatService.MessageType.ERROR, Map.of("0", target.getName(), "1", formatDuration(remainingMs))));
        }
        return true;
    }

    private String formatDuration(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        if (days > 0) return days + "d " + (hours % 24) + "h";
        if (hours > 0) return hours + "h " + (minutes % 60) + "m";
        if (minutes > 0) return minutes + "m " + (seconds % 60) + "s";
        return seconds + "s";
    }
}
