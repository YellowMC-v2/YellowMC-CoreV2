package de.emn4tor.modules.muzzle;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.RedisManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

public class ChatManager {
    private final RedisManager redis = RedisManager.getInstance();
    private JedisPubSub activePubSub; // Reference to stop the listener

    public ChatManager() {
        this.activePubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                // Return to main thread for Bukkit API calls
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
        };

        // Start the subscriber
        redis.subscribe("chat", activePubSub);
    }

    public void sendMessage(String msg) {
        redis.publish("chat", msg);
    }

    public void shutdown() {
        if (activePubSub != null && activePubSub.isSubscribed()) {
            activePubSub.unsubscribe();
        }
    }
}