package de.emn4tor.modules.playtime;

/*
 *  @author: Emn4tor
 *  @created: 18.08.2025
 */

import de.emn4tor.utils.TimeFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlaytimeCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final PlaytimeManager playTimeManager;

    // Messages
    private static final String PREFIX = "<gray>[<gold>PlayTime<gray>] ";
    private static final String NO_PERMISSION = PREFIX + "<red>You don't have permission to use this command.";
    private static final String PLAYER_NOT_FOUND = PREFIX + "<red>Player not found.";
    private static final String PLAYTIME_SELF = PREFIX + "<yellow>Your playtime: <green>%s";
    private static final String PLAYTIME_OTHER = PREFIX + "<yellow>%s's playtime: <green>%s";
    private static final String CONSOLE_ERROR = PREFIX + "<red>This command can only be used by players.";

    public PlaytimeCommand(JavaPlugin plugin, PlaytimeManager playTimeManager) {
        this.plugin = plugin;
        this.playTimeManager = playTimeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendRichMessage(CONSOLE_ERROR);
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("playtime.use")) {
            player.sendRichMessage(NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            // Show own playtime - this will now trigger an update
            long playTime = playTimeManager.getPlayTime(player.getUniqueId());
            player.sendRichMessage(String.format(PLAYTIME_SELF, TimeFormatter.format(playTime)));
            return true;
        }

        // Check permission for viewing others' playtime
        if (!player.hasPermission("playtime.other")) {
            player.sendRichMessage(NO_PERMISSION);
            return true;
        }

        // Show other player's playtime
        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendRichMessage(PLAYER_NOT_FOUND);
            return true;
        }

        // This will now trigger an update for the target player
        long playTime = playTimeManager.getPlayTime(target.getUniqueId());
        player.sendRichMessage(String.format(PLAYTIME_OTHER, target.getName(), TimeFormatter.format(playTime)));

        return true;
    }
}
