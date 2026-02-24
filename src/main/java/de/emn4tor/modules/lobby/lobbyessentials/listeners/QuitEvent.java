package de.emn4tor.modules.lobby.lobbyessentials.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuitEvent implements Listener {

    @EventHandler
    public void onQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        event.quitMessage(null);
    }
}
