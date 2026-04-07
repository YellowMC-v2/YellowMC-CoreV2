package de.emn4tor.modules.global.tpa.services;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.RedisManager;
import de.emn4tor.modules.global.tpa.api.TeleportAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public final class TPAService {
    private final YellowMCCoreV2 core;
    private final TeleportAPI teleportAPI;
    private final RedisManager redis = RedisManager.getInstance();

    public TPAService(YellowMCCoreV2 core, TeleportAPI teleportAPI) {
        this.core = core;
        this.teleportAPI = teleportAPI;

        this.setupRedisListener();
    }

    public void sendRequest(@NonNull Player sender, @NonNull String targetName) {
        redis.publish("tpa_system", "REQUEST_INCOMING:" + targetName + ":" + sender.getName() + ":" + sender.getUniqueId());

        sender.sendMessage("TPA-Anfrage an " + targetName + " verschickt.");
    }

    public void acceptRequest(@NonNull Player target, @NonNull String senderName) {
        var senderUUIDString = this.redis.get("tpa:req:" + target.getUniqueId());

        if (senderUUIDString != null) {
            var myServerId = this.core.getConfig().getString("server-name");

            this.redis.setTemporary("tpa:pending_teleport:" + senderUUIDString, target.getUniqueId().toString(), 30);
            this.redis.publish("tpa_system", "REQUEST_ACCEPTED:" + senderUUIDString + ":" + myServerId);
            this.redis.delete("tpa:req:" + target.getUniqueId());

            target.sendMessage("§aAnfrage angenommen.");
        } else {
            target.sendMessage("§cKeine aktive Anfrage gefunden.");
        }
    }

    private void setupRedisListener() {
        this.redis.subscribe("tpa_system", new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                var parts = message.split(":");
                var action = parts[0];

                if (action.equals("REQUEST_INCOMING")) {
                    var targetName = parts[1];
                    var senderName = parts[2];
                    var senderUUID = UUID.fromString(parts[3]);

                    var target = Bukkit.getPlayer(targetName);

                    if (target != null) {
                        redis.setTemporary("tpa:req:" + target.getUniqueId(), senderUUID.toString(), 60 * 5);

                        target.sendMessage(senderName + " möchte sich zu dir teleportieren.");
                        target.sendMessage("Nutze /tpaaccept " + senderName);
                    }
                }

                if (action.equals("REQUEST_ACCEPTED")) {
                    var senderUUID = UUID.fromString(parts[1]);
                    var targetServer = parts[2];

                    var sender = Bukkit.getPlayer(senderUUID);
                    if (sender != null) {
                        sender.sendMessage("Angeommen");
                        teleportAPI.teleportToRemoteServer(sender, targetServer);
                    }
                }
            }
        });
    }
}
