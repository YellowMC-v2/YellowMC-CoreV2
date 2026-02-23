package de.emn4tor.commons;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerConnector {

    private final Plugin plugin;

    public ServerConnector(Plugin plugin) {
        this.plugin = plugin;
    }

    public void sendToServer(Player player, String serverName) {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(byteOut)) {

            out.writeUTF("Connect");
            out.writeUTF(serverName);
            player.sendPluginMessage(plugin, "BungeeCord", byteOut.toByteArray());
        } catch (IOException e) {
            plugin.getLogger().warning("Error while sending player to server: " + e.getMessage());
        }
    }
}