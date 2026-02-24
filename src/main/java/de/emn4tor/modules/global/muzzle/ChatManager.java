package de.emn4tor.modules.global.muzzle;

/*
 *  @author: Emn4tor
 *  @created: 02.05.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.RedisManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

public class ChatManager {
    private final RedisManager redis = RedisManager.getInstance();

    public ChatManager() {
        redis.subscribe("chat", new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                Bukkit.getScheduler().runTask(YellowMCCoreV2.getInstance(), () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.hasPermission("core.chat.debug")) {
                            player.sendMessage(MiniMessage.miniMessage().deserialize(
                                    "<gray>[<green>Redis</green>] <gray>" + message + "</gray>"
                            ));
                        } else {
                            player.sendMessage(MiniMessage.miniMessage().deserialize(
                                    "<gray>" + message + "</gray>"
                            ));
                        }
                    }
                });
            }
        });
    }

    public void sendMessage(String msg) {
        redis.publish("chat", msg);
    }

    public void shutdown() {
        redis.close();
    }
}
