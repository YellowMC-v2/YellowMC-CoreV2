package de.emn4tor.modules.playtime;

/*
 *  @author: Emn4tor
 *  @created: 18.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import de.emn4tor.utils.TimeFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class PlaytimeCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final PlaytimeManager playTimeManager;


    public PlaytimeCommand(JavaPlugin plugin, PlaytimeManager playTimeManager) {
        this.plugin = plugin;
        this.playTimeManager = playTimeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // Show own playtime - this will now trigger an update
            long playTime = playTimeManager.getPlayTime(player.getUniqueId());
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "playtime-self", FormatService.MessageType.SYSTEM, Map.of("0", TimeFormatter.format(playTime))));
            return true;
        }

        // Show other player's playtime
        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "error-target-not-online", FormatService.MessageType.SYSTEM));
            return true;
        }

        // This will now trigger an update for the target player
        long playTime = playTimeManager.getPlayTime(target.getUniqueId());
        player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "playtime-other", FormatService.MessageType.SYSTEM, Map.of("0", target.getName(), "1", TimeFormatter.format(playTime))));
        return true;
    }
}
