package de.emn4tor.modules.commands.social;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DiscordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Unseren Discord-Server findest du unter: <click:open_url:'https://www.youtube.com/@YellowMCNetzwerk'><hover:show_text:'<gray>Klicke, um zum Discord-Server zu gelangen.'><color:#00cc66>https://www.youtube.com/@YellowMCNetzwerk</hover></click>"));
        return true;
    }
}
