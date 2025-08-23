package de.emn4tor.modules.economy.coins.api;

/*
 *  @author: Emn4tor
 *  @created: 20.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.SQLManager;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class EconomyManager {

    private final YellowMCCoreV2 plugin;
    private final int startingBalance;
    private final ExecutorService executorService;

    public EconomyManager(YellowMCCoreV2 plugin) {
        this.plugin = plugin;
        this.startingBalance = plugin.getConfig().getInt("economy.starting-balance", 100);
        this.executorService = Executors.newCachedThreadPool();
    }

    public void initialize() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS player_coins (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "coins INT NOT NULL DEFAULT 0);";

        CompletableFuture.runAsync(() -> {
            try (Connection conn = SQLManager.getInstance().getConnection();
                 Statement stmt = conn.createStatement()) {

                stmt.execute(createTableQuery);

            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while initializing player_coins table", e);
            }
        }, executorService);
    }

    public void setCoins(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        String query = "UPDATE player_coins SET coins = ? WHERE uuid = ?;";

        CompletableFuture.runAsync(() -> {
            try (Connection conn = SQLManager.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, amount);
                stmt.setString(2, uuid.toString());
                stmt.executeUpdate();

            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while setting coins for player with UUID: " + uuid, e);
            }
        }, executorService);
    }

    public void addCoins(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        getCoins(uuid).thenAccept(currentCoins -> setCoins(player, currentCoins + amount));
    }

    public CompletableFuture<Boolean> removeCoins(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        return getCoins(uuid).thenApply(currentCoins -> {
            if (currentCoins >= amount) {
                setCoins(player, currentCoins - amount);
                return true;
            }
            return false;
        });
    }

    public CompletableFuture<Boolean> transferCoins(Player from, Player to, int amount) {
        return removeCoins(from, amount).thenApply(success -> {
            if (success) {
                addCoins(to, amount);
                return true;
            }
            return false;
        });
    }

    public CompletableFuture<Integer> getCoins(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "SELECT coins FROM player_coins WHERE uuid = ?;";
            try (Connection conn = SQLManager.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, uuid.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("coins");
                    } else {
                        createAccount(uuid, startingBalance);
                        return startingBalance;
                    }
                }

            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while getting coins for player with UUID: " + uuid, e);
                return 0;
            }
        }, executorService);
    }

    public CompletableFuture<Boolean> hasEnoughCoins(UUID uuid, int amount) {
        return getCoins(uuid).thenApply(coins -> coins >= amount);
    }

    public void createAccount(UUID uuid, int coins) {
        String query = "INSERT INTO player_coins (uuid, coins) VALUES (?, ?) ON DUPLICATE KEY UPDATE coins = coins;";

        CompletableFuture.runAsync(() -> {
            try (Connection conn = SQLManager.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, uuid.toString());
                stmt.setInt(2, coins);
                stmt.executeUpdate();

            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while creating account for player with UUID: " + uuid, e);
            }
        }, executorService);
    }

    public CompletableFuture<Boolean> hasAccount(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "SELECT 1 FROM player_coins WHERE uuid = ?;";
            try (Connection conn = SQLManager.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, uuid.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }

            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while checking account for player with UUID: " + uuid, e);
                return false;
            }
        }, executorService);
    }
}
