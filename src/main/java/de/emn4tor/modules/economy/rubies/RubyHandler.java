package de.emn4tor.modules.economy.rubies;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.SQLManager;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class RubyHandler {

    private static YellowMCCoreV2 plugin;

    public static void init(YellowMCCoreV2 core) {
        plugin = core;
    }
    public static void initialize() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS player_rubies (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "rubies INT DEFAULT 0)";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableQuery);
            plugin.getLogger().info("Successfully created or verified player_rubies table");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create player_rubies table: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize RubyHandler", e);
        }
        }

    private static Connection getConnection() throws SQLException {
        return SQLManager.getInstance().getConnection();
    }

    public static CompletableFuture<Integer> getRubiesAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "SELECT rubies FROM player_rubies WHERE uuid = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, uuid.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("rubies");
                    } else {
                        createAccount(uuid, 0);
                        return 0;
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error fetching rubies for UUID: " + uuid, e);
                return 0;
            }
        });
    }

    public static void setRubies(UUID uuid, int amount) {
        CompletableFuture.runAsync(() -> {
            String query = "UPDATE player_rubies SET rubies = ? WHERE uuid = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, amount);
                stmt.setString(2, uuid.toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error setting rubies for UUID: " + uuid, e);
            }
        });
    }

    public static void addRubies(UUID uuid, int amount) {
        getRubiesAsync(uuid).thenAccept(current -> setRubies(uuid, current + amount));
    }

    public static void removeRubies(UUID uuid, int amount) {
        getRubiesAsync(uuid).thenAccept(current -> {
            int newAmount = Math.max(current - amount, 0);
            setRubies(uuid, newAmount);
        });
    }

    public static CompletableFuture<Boolean> payRubies(UUID uuid, int amount) {
        return getRubiesAsync(uuid).thenApply(current -> {
            if (current >= amount) {
                setRubies(uuid, current - amount);
                return true;
            } else {
                return false;
            }
        });
    }

    public static void createAccount(UUID uuid, int rubies) {
        String query = "INSERT INTO player_rubies (uuid, rubies) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, uuid.toString());
            stmt.setInt(2, rubies);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error creating ruby account for UUID: " + uuid, e);
        }
    }
}
