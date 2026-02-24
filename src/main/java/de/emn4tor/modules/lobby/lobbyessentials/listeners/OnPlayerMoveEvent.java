package de.emn4tor.modules.lobby.lobbyessentials.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnPlayerMoveEvent implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        if (loc.getY() < 22  && loc.getWorld().getName().equals("world")) {
            Location newLoc = new Location(Bukkit.getWorld("world"), 0, 65, 0, -180, 0);
            player.teleport(newLoc);
        }
    }
}