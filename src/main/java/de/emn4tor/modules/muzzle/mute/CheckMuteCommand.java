package de.emn4tor.modules.muzzle.mute;

/*
 *  @author: Emn4tor
 *  @created: 04.05.2025
 */

import de.emn4tor.YellowMCCoreV2;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CheckMuteCommand implements CommandExecutor {
    private final MuteManager muteManager;

    public CheckMuteCommand(YellowMCCoreV2 plugin) {
        this.muteManager = new MuteManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) return false;

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        UUID uuid = target.getUniqueId();
        if (!muteManager.isMuted(uuid)) {
            sender.sendMessage("§a" + target.getName() + " is not muted.");
            return true;
        }

        long expiresAt = muteManager.getExpiresAt(uuid);
        if (expiresAt == 0) {
            sender.sendMessage("§e" + target.getName() + " is permanently muted.");
        } else {
            long remainingMs = expiresAt - System.currentTimeMillis();
            if (remainingMs < 0) remainingMs = 0;
            sender.sendMessage("§e" + target.getName() + " is muted for another " + formatDuration(remainingMs) + ".");
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
