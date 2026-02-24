package de.emn4tor.modules.global.muzzle.mute;

/*
 *  @author: Emn4tor
 *  @created: 04.05.2025
 */

import de.emn4tor.YellowMCCoreV2;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class UnmuteCommand implements CommandExecutor {
    private final MuteManager muteManager;

    public UnmuteCommand(YellowMCCoreV2 plugin) {
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

        muteManager.unmute(target.getUniqueId());
        sender.sendMessage("§aUnmuted " + target.getName());
        return true;
    }
}