package de.emn4tor.modules.muzzle;

/*
 *  @author: Emn4tor
 *  @created: 04.05.2025
 */

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ClearChatCommand implements CommandExecutor {
    LuckPerms api = LuckPermsProvider.get();
    private final ChatManager chatManager;

    public ClearChatCommand(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        for (int i = 0; i < 100; i++) {
            chatManager.sendMessage(" ");
        }
        chatManager.sendMessage("<yellow>Chat cleared by" + sender.getName() + "<hover:show_text:'Messages visible to everyone are harder to translate, that is why we did not translate this message'>(Why no translation?)</hover>");
        return true;
    }
}
