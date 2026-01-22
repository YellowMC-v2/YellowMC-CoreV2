package de.emn4tor.modules.commands.social;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class YouTubeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "social-youtube", FormatService.MessageType.SYSTEM, Map.of("0", "<click:open_url:'https://www.youtube.com/@YellowMCNetzwerk'><hover:show_text:'https://www.youtube.com/@YellowMCNetzwerk'>https://www.youtube.com/@YellowMCNetzwerk</hover></click>")));
        return true;
    }
}
