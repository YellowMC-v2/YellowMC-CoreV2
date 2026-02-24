package de.emn4tor.modules.global.muzzle.bans;

/*
 *  @author: Emn4tor
 *  @created: 08.05.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class KickCommand implements CommandExecutor {
    private final BanManager banManager;
    private final YellowMCCoreV2 plugin;

    public KickCommand(BanManager banManager, YellowMCCoreV2 plugin) {
        this.banManager = banManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Usage: /kick <player> <reason>");
            return true;
        }
        String playerName = args[0];
        String reason = args[1];
        if (!(sender instanceof Player player)) {
            banManager.kickPlayer(playerName, reason, "System");
            return true;
        }
        banManager.kickPlayer(playerName, reason, sender.getName());
        player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "furniture-shop-purchase-success", FormatService.MessageType.SYSTEM, Map.of("0", playerName, "1", reason)));
        return true;
    }
}
