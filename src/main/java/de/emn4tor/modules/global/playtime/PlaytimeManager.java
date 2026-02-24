package de.emn4tor.modules.global.playtime;

/*
 *  @author: Emn4tor
 *  @created: 18.08.2025
 */

import de.emn4tor.data.SQLManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class PlaytimeManager {
    private final JavaPlugin plugin;
    private final Map<UUID, Long> cachedPlayTime = new HashMap<>();
    private final Map<UUID, Long> startTime = new HashMap<>();

    public PlaytimeManager(JavaPlugin plugin) {
        this.plugin = plugin;

        createTableIfNotExists();
        startPeriodicCacheUpdate();
    }

    private void startPeriodicCacheUpdate() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                updatePlayTimeInDatabase(uuid);
            }
        }, 6000L, 6000L); // every 5 min
    }

    private void createTableIfNotExists() {
        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS playtime (" +
                             "player_uuid VARCHAR(36) PRIMARY KEY, " +
                             "playtime BIGINT DEFAULT 0)")) {
            stmt.executeUpdate();
            plugin.getLogger().info("Successfully created or verified playtime table");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create playtime table: " + e.getMessage(), e);
        }
    }

    public void loadFromDatabase(UUID uuid) {
        if (!existsInDatabase(uuid)) {
            update("INSERT INTO playtime (player_uuid) VALUES (?)", uuid.toString());
            cachedPlayTime.put(uuid, 0L);
            return;
        }

        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT playtime FROM playtime WHERE player_uuid=?")) {
            stmt.setString(1, uuid.toString());
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                cachedPlayTime.put(uuid, result.getLong("playtime"));
            }
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load playtime for player " + uuid, exception);
        }
    }

    public void updatePlayTime(UUID uuid) {
        updatePlayTimeInDatabase(uuid);
    }

    private void updatePlayTimeInDatabase(UUID uuid) {
        Long sessionStartTime = startTime.get(uuid);
        if (sessionStartTime == null) {
            plugin.getLogger().warning("No start time found for player " + uuid);
            return;
        }

        long sessionTime = System.currentTimeMillis() - sessionStartTime;
        long storedPlaytime = cachedPlayTime.getOrDefault(uuid, 0L);
        long newPlaytime = storedPlaytime + sessionTime;

        update("UPDATE playtime SET playtime=? WHERE player_uuid=?", newPlaytime, uuid.toString());

        cachedPlayTime.put(uuid, newPlaytime);
        startTime.put(uuid, System.currentTimeMillis());
    }

    private void update(String sql, Object... params) {
        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to execute update: " + sql, e);
        }
    }

    public long getPlayTime(UUID uuid) {
        if (startTime.containsKey(uuid)) {
            updatePlayTimeInDatabase(uuid);
        }

        if (!cachedPlayTime.containsKey(uuid)) {
            loadFromDatabase(uuid);
        }

        return cachedPlayTime.getOrDefault(uuid, 0L);
    }

    public long getCurrentPlayTime(UUID uuid) {
        if (!cachedPlayTime.containsKey(uuid)) {
            loadFromDatabase(uuid);
            return cachedPlayTime.getOrDefault(uuid, 0L);
        }

        if (startTime.containsKey(uuid)) {
            long sessionTime = System.currentTimeMillis() - startTime.get(uuid);
            return cachedPlayTime.get(uuid) + sessionTime;
        }

        return cachedPlayTime.get(uuid);
    }

    private boolean existsInDatabase(UUID uuid) {
        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT player_uuid FROM playtime WHERE player_uuid=?")) {
            stmt.setString(1, uuid.toString());
            ResultSet result = stmt.executeQuery();
            return result.next();
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE, "Failed to check if player exists in database", exception);
            return false;
        }
    }

    public Map<UUID, Long> startTime() {
        return startTime;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
