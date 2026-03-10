package de.emn4tor.modules.global.tpa.api;

import com.google.common.io.ByteStreams;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.RedisManager;
import org.bukkit.Bukkit;
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

    public void teleport(@NonNull Player player, @NonNull Player target) {
        var playerUUID = player.getUniqueId();

        if (this.tasks.containsKey(playerUUID)) {
            return;
        }

        var startLocation = player.getLocation();

        var runnable = new BukkitRunnable() {
            private int time = COUNTDOWN_SECONDS;

            @Override
            public void run() {
                if (!player.isOnline() || !target.isOnline()) {
                    tasks.remove(playerUUID);
                    this.cancel();
                    return;
                }

                if (!player.getLocation().getWorld().equals(startLocation.getWorld()) ||
                        player.getLocation().distanceSquared(startLocation) > 1) {
                    tasks.remove(playerUUID);
                    this.cancel();
                    return;
                }

                if (time <= 0) {
                    tasks.remove(playerUUID);
                    this.cancel();

                    player.teleportAsync(target.getLocation());
                    return;
                }

                time--;
            }
        };

        runnable.runTaskTimer(this.core, 0, 20);
        this.tasks.put(playerUUID, runnable);
    }

    public void teleportToRemoteServer(@NonNull Player player, String targetServerId) {
        var startLocation = player.getLocation();
        var serverId = this.core.getConfig().getString("server-name");

        new BukkitRunnable() {
            private int time = COUNTDOWN_SECONDS;

            @Override
            public void run() {
                if (!player.isOnline()) { this.cancel(); return; }

                if (player.getLocation().distanceSquared(startLocation) > 1) {
                    player.sendMessage("Teleport abgebrochen, du hast dich bewegt.");
                    this.cancel();
                    return;
                }

                if (time <= 0) {
                    this.cancel();

                    if (targetServerId.equals(serverId)) {
                        player.sendMessage("GLEICHER SERVER");
                        var targetUUIDString = RedisManager.getInstance().get("tpa:pending_teleport:" + player.getUniqueId());

                        if (targetUUIDString != null) {
                            player.sendMessage("UUIDSTRING");
                            player.sendMessage(targetUUIDString);
                            var target = Bukkit.getPlayer(UUID.fromString(targetUUIDString));

                            player.sendMessage(target == null ? "NULL" : target.toString());

                            if (target != null) {
                                player.sendMessage("TARGET");
                                player.teleportAsync(target.getLocation());
                                player.sendMessage("Teleport erfolgreich!");

                                RedisManager.getInstance().delete("tpa:pending_teleport:" + player.getUniqueId());

                                return;
                            }
                        }
                    } else {
                        sendToData(player, targetServerId);
                    }
                    return;
                }

                player.sendMessage("Teleport in " + time + "...");
                time--;
            }
        }.runTaskTimer(core, 0, 20);
    }

    private void sendToData(@NonNull Player player, String server) {
        var out = ByteStreams.newDataOutput();

        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(core, "BungeeCord", out.toByteArray());
    }
}
