package de.emn4tor.utils.bridge;

import com.google.common.io.ByteStreams;
import de.emn4tor.YellowMCCoreV2;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import redis.clients.jedis.JedisPubSub;

public final class NetworkBridge {
    private final YellowMCCoreV2 core;

    public NetworkBridge(YellowMCCoreV2 core) {
        this.core = core;
    }

    /**
     * Sends a player to a server and executes a command on it
     *
     * @param player the target player
     * @param serverName the target server a player will be sent
     * @param command the command which is executed when a player joined the serverName-Server
     */
    public void sendWithCommand(@NonNull Player player, @NotNull String serverName, @NotNull String command) {
        var out = ByteStreams.newDataOutput();

        out.writeUTF("Connect");
        out.writeUTF(serverName);

        player.sendPluginMessage(this.core, "BungeeCord", out.toByteArray());

        var redisKey = player.getName() + ":" + command;

        Bukkit.getScheduler().runTaskAsynchronously(this.core, () -> {
            YellowMCCoreV2.getRedisManager().publish("server-commands-" + serverName, redisKey);
        });
    }

    public void startRedisListener() {
        Bukkit.getScheduler().runTaskAsynchronously(this.core, () -> {
            var serverName = this.core.getConfig().getString("server-id");

            YellowMCCoreV2.getRedisManager().subscribe("server-commands-" + serverName, new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    var parts = message.split(":" ,2);
                    var playerName = parts[0];
                    var command = parts[1];

                    Bukkit.getScheduler().runTask(core, () -> {
                        executeCommand(playerName, command, 0);
                    });
                }
            });
        });
    }

    private void executeCommand(String playerName, String command, int attempt) {
        var player = Bukkit.getPlayer(playerName);

        if (player != null && player.isOnline()) {
            player.performCommand(command.replace("/", ""));
            this.core.getLogger().info("CHECK CHECK");
        } else if (attempt < 10) {
            Bukkit.getScheduler().runTaskLater(this.core, () -> {
                executeCommand(playerName, command, attempt + 1);
            }, 20L);
        }
    }
}
