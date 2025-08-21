package de.emn4tor.modules.muzzle.bans;

/*
 *  @author: Emn4tor
 *  @created: 08.05.2025
 */

import de.emn4tor.YellowMCCoreV2;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BanCommand implements CommandExecutor {
    private final BanManager banManager;
    private final YellowMCCoreV2 plugin;

    public BanCommand(BanManager banManager, YellowMCCoreV2 plugin) {
        this.banManager = banManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Usage: /ban <player> <reason> [<duration>]");
            return true;
        }
        String playerName = args[0];

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(playerName);
        UUID uuid = targetPlayer.getUniqueId();

        String reason = args[1];
        String duration = args.length > 2 ? args[2] : null;

        banManager.banPlayer(sender.getName(), uuid.toString(), reason, duration);
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Der Spieler <yellow>" + playerName + "</yellow>mit der UUID <yellow>" + uuid + "</yellow> wurde für <yellow>" + reason + "</yellow> gebannt</green>"));
        return true;
    }
}
