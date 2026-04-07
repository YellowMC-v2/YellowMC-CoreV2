package de.emn4tor.modules.global.tpa.listener;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.RedisManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class TPAListener implements Listener {
    private final RedisManager redis = RedisManager.getInstance();

    @EventHandler
    public void handle(@NotNull  PlayerJoinEvent event) {
        var player = event.getPlayer();
        var targetUUIDString = redis.get("tpa:pending_teleport:" + player.getUniqueId());

        if (targetUUIDString != null) {
            var targetUUID = UUID.fromString(targetUUIDString);

            Bukkit.getScheduler().runTaskLater(YellowMCCoreV2.getInstance(), () -> {
                var target = Bukkit.getPlayer(targetUUID);

                if (target != null) {
                    player.teleportAsync(target.getLocation());
                    player.sendMessage("§aWillkommen! Du wurdest zu deinem Ziel teleportiert.");
                }

                redis.delete("tpa:pending_teleport:" + player.getUniqueId());
            }, 5L);
        }
    }
}