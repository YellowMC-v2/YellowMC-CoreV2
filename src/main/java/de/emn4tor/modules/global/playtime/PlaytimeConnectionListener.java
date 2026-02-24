package de.emn4tor.modules.global.playtime;

/*
 *  @author: Emn4tor
 *  @created: 18.08.2025
 */

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PlaytimeConnectionListener implements Listener {
    private final JavaPlugin plugin;
    private final PlaytimeManager playTimeManager;

    public PlaytimeConnectionListener(JavaPlugin plugin, PlaytimeManager playTimeManager) {
        this.plugin = plugin;
        this.playTimeManager = playTimeManager;

        // Register events
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uniqueId = player.getUniqueId();

        // Record the time when player joins
        playTimeManager.startTime().put(uniqueId, System.currentTimeMillis());

        // Load player's playtime data asynchronously
        plugin.getServer().getScheduler().runTaskAsynchronously(
                plugin,
                () -> playTimeManager.loadFromDatabase(uniqueId)
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID uniqueId = player.getUniqueId();

        // Update player's playtime data asynchronously when they leave
        plugin.getServer().getScheduler().runTaskAsynchronously(
                plugin,
                () -> {
                    playTimeManager.updatePlayTime(uniqueId);
                    playTimeManager.startTime().remove(uniqueId);
                }
        );
    }
}
