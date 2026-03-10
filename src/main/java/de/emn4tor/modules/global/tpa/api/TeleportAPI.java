package de.emn4tor.modules.global.tpa.api;

import de.emn4tor.YellowMCCoreV2;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This API is used to teleport a player with a specific cooldown
 *
 */
public final class TeleportAPI {
    private final YellowMCCoreV2 core;
    private final Map<UUID, BukkitRunnable> tasks;

    public static final int COUNTDOWN_SECONDS = 5;

    public TeleportAPI(YellowMCCoreV2 core) {
        this.core = core;
        this.tasks = new ConcurrentHashMap<>();
    }

    /**
     * Teleports a player to a specific location with a cooldown
     *
     * @param player the player which will be teleported
     * @param location the target location
     */
    public void teleport(@NonNull Player player, @NonNull Location location) {
        var playerUUID = player.getUniqueId();

        if (this.tasks.containsKey(playerUUID)) {
            return;
        }

        var startLocation = player.getLocation();

        var runnable = new BukkitRunnable() {
            private int time = COUNTDOWN_SECONDS;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    tasks.remove(playerUUID);
                    this.cancel();
                    return;
                }

                if (!player.getLocation().getWorld().equals(startLocation.getWorld()) ||
                        player.getLocation().distanceSquared(startLocation) > 1) {
                    tasks.remove(playerUUID);
                    this.cancel();

                    //TODO: send player a message that teleportation was canceled
                    return;
                }

                if (time <= 0) {
                    tasks.remove(playerUUID);
                    this.cancel();

                    player.teleportAsync(location);

                    //TODO: send player a message when teleportation was successfully
                    return;
                }

                player.sendMessage("JA");
                time--;
            }
        };

        runnable.runTaskTimer(this.core, 0,20);
        this.tasks.put(playerUUID, runnable);
    }
}
