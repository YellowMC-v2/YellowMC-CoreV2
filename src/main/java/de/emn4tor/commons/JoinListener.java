package de.emn4tor.commons;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.global.commands.languagesel.LanguageCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Record the time the player joined
        int joinTime = (int) System.currentTimeMillis();
        event.setJoinMessage(null);
        Bukkit.getServer().broadcast(MiniMessage.miniMessage().deserialize("<gray>[<green>+<gray>] <white>" + player.getName()));
        if (YellowMCCoreV2.getLocaleService().getLocaleNoFallBack(player.getUniqueId()) == null) {
            LanguageCommand.openLangGUI(player, 0);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.quitMessage(MiniMessage.miniMessage().deserialize("<gray>[<red>-<gray>] <white>" + player.getName()));

    }
}