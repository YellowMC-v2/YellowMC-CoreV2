package de.emn4tor.modules.muzzle;

/*
 *  @author: Emn4tor
 *  @created: 04.05.2025
 */


import de.emn4tor.modules.muzzle.mute.MuteManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.regex.Pattern;

import static de.emn4tor.modules.muzzle.MuzzleModule.bannedWords;

public class ChatListener implements Listener {
    LuckPerms api = LuckPermsProvider.get();
    private final MuteManager muteManager;



    private final ChatManager chatManager;

    public ChatListener(MuteManager muteManager, ChatManager chatManager) {
        this.muteManager = muteManager;
        this.chatManager = chatManager;
    }


    @EventHandler
    public void onChatMessage(AsyncChatEvent event){
        event.setCancelled(true);
        Player player = event.getPlayer();
        User user = api.getUserManager().getUser(player.getUniqueId());
        String prefix = user.getCachedData().getMetaData().getPrefix();
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        if (muteManager.isMuted(player.getUniqueId())) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    "[<yellow>MaulKorb</yellow>] <gray>Du bist für <red>"
                            + muteManager.getRemainingTime(player.getUniqueId())
                            + "</red> <gray>stummgeschaltet! Grund: <dark_red>"
                            + muteManager.getReason(event.getPlayer().getUniqueId())
                            + "</dark_red></gray>"
            ));
            return;
        }
        if (containsBannedWord(message)) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("[<yellow>MaulKorb</yellow>] <red>Bitte benutze keine Schimpfwörter!")); //TODO: Mute player?
            return;
        }
        if (!player.hasPermission("core.chat.color")) {
            if (message.contains("<") && message.contains(">")) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Tipp:</yellow> <gray>Du kannst keine Farben im Chat verwenden, dies ist ab dem <#c0c0c0>Silber Rang<gray>, oder mit der <rainbow>Farbig schreiben</rainbow> Belohnung aus den Crates möglich.</gray>"));
            }
            message = message.replaceAll("<[^>]*>", "");
        }

        chatManager.sendMessage("<reset>" + prefix + " " + player.getName() + "<white>:<gray> " + message );
    }

    private boolean containsBannedWord(String message) {
        String lower = message.toLowerCase();
        for (String banned : bannedWords) {
            String pattern = "(?<![a-z0-9äöüß])" + fuzzyWordRegex(banned) + "(?![a-z0-9äöüß])";
            if (Pattern.compile(pattern).matcher(lower).find()) {
                return true;
            }
        }
        return false;
    }

    private String fuzzyWordRegex(String word) {
        StringBuilder regex = new StringBuilder();
        for (char c : word.toLowerCase().toCharArray()) {
            regex.append(Pattern.quote(String.valueOf(c)))
                    .append("[\\s\\p{Punct}_]{0,2}");
        }
        return regex.toString();
    }




}
