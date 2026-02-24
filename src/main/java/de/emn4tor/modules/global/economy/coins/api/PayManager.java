package de.emn4tor.modules.global.economy.coins.api;

/*
 *  @author: Emn4tor
 *  @created: 20.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import de.emn4tor.data.RedisManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.UUID;

public class PayManager {
    private final Plugin plugin;
    private final RedisManager redis = RedisManager.getInstance();

    public PayManager(Plugin plugin) {
        this.plugin = plugin;
        registerRedisPayListener();
    }

    private void registerRedisPayListener() {
        redis.subscribe("yellowmc:pay", new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    JSONObject json = new JSONObject(message);
                    UUID toUUID = UUID.fromString(json.getString("to"));
                    UUID fromUUID = UUID.fromString(json.getString("from"));
                    int amount = json.getInt("amount");
                    String fromName = json.getString("fromName");

                    OfflinePlayer recipient = Bukkit.getOfflinePlayer(toUUID);
                    EconomyHandler.addCoins(recipient, amount);

                    if (recipient.isOnline()) {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            Player online = (Player) recipient;
                            online.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(online.getUniqueId(), "economy-pay-receive", FormatService.MessageType.SYSTEM, Map.of("0", String.valueOf(amount), "1", fromName)));
                        });
                    }

                    Bukkit.getLogger().info("[Redis] " + fromName + " paid " + amount + " to " + recipient.getName());

                } catch (Exception e) {
                    Bukkit.getLogger().severe("[Redis] Failed to handle pay message: " + message);
                    e.printStackTrace();
                }
            }
        });
    }
}
