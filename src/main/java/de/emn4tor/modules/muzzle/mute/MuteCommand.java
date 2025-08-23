package de.emn4tor.modules.muzzle.mute;

/*
 *  @author: Emn4tor
 *  @created: 04.05.2025
 */

import de.emn4tor.YellowMCCoreV2;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class MuteCommand implements CommandExecutor {
    private final MuteManager muteManager;

    public MuteCommand(YellowMCCoreV2 plugin) {
        this.muteManager = new MuteManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) return false;

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        String reason = args[1];
        long durationMillis = args.length >= 3 ? parseDuration(args[2]) : 0;

        muteManager.mute(target.getUniqueId(), reason, sender.getName(), durationMillis == 0 ? 0 : System.currentTimeMillis() + durationMillis);
        sender.sendMessage("§aMuted " + target.getName() + " for: " + reason);
        return true;
    }

    private long parseDuration(String input) {
        if (input.endsWith("m")) return Long.parseLong(input.replace("m", "")) * 60 * 1000;
        if (input.endsWith("h")) return Long.parseLong(input.replace("h", "")) * 60 * 60 * 1000;
        if (input.endsWith("d")) return Long.parseLong(input.replace("d", "")) * 24 * 60 * 60 * 1000;
        return Long.parseLong(input); // raw ms fallback
    }
}
