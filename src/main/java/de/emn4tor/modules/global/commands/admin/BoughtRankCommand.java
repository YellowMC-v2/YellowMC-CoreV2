package de.emn4tor.modules.global.commands.admin;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.modules.global.muzzle.ChatManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BoughtRankCommand implements CommandExecutor {
    private final ChatManager chatManager;

    public BoughtRankCommand(ChatManager chatManager) {
        this.chatManager = chatManager;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2){
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>/boughtrank <player> <displayname>"));
            return false;
        }
        String playerName = args[0];
        String rank = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        chatManager.sendMessage("<gradient:yellow:#ff7b00>⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯<br><yellow>» <gold>" + playerName + " <reset>hat gerade " + rank + " <reset>gekauft!<br><yellow>» <gray>Hol dir jetzt deinen Rang auf <gold>shop.yellowmc.de<br><gradient:yellow:#ff7b00>⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯<br>");
        return true;
    }
}
