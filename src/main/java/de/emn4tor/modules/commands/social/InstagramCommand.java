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

public class InstagramCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Unseren Instagram-Account findest du unter: <click:open_url:'https://www.instagram.com/yellowmc_de'><hover:show_text:'<gray>Klicke, um zum Instagram-Account zu gelangen.'><color:#00cc66>https://www.instagram.com/yellowmc_de</hover></click>"));
        return true;
    }
}
