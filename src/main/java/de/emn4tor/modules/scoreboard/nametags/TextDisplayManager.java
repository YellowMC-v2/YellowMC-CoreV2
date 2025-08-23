package de.emn4tor.modules.scoreboard.nametags;

/*
 *  @author: Emn4tor
 *  @created: 28.05.2025
 */

import org.bukkit.entity.Player;

public class TextDisplayManager {

    public void setNametag(Player player, String name) {
        // This method should set the nametag for the player.
        // Implementation will depend on the server version and plugins used.
        // For example, using a plugin like ProtocolLib or a custom nametag system.
        player.setPlayerListName(name);
    }
}
