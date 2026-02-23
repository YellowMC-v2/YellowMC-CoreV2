package de.emn4tor.modules.furnitureshop;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.SQLManager;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class FurnitureShopManager {

    private static final Map<String, Integer> furniturePriceMap = new HashMap<>();

    public void createFurniturePricesTable() {
        String query = "CREATE TABLE IF NOT EXISTS furniture_prices (" +
                "furnitureID VARCHAR(255) PRIMARY KEY, " +
                "price INT NOT NULL" +
                ")";

        try (Connection connection = SQLManager.getInstance().getConnection();
             java.sql.Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadFurniturePrices() {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = SQLManager.getInstance().getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT furnitureID, price FROM furniture_prices");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String furnitureId = rs.getString("furnitureID");
                    int price = rs.getInt("price");
                    furniturePriceMap.put(furnitureId, price);
                }

                YellowMCCoreV2.getPlugin(YellowMCCoreV2.class)
                        .getLogger().info("Loaded " + furniturePriceMap.size() + " furniture prices from the database.");

            } catch (SQLException e) {
                YellowMCCoreV2.getPlugin(YellowMCCoreV2.class)
                        .getLogger().log(Level.SEVERE, "Error loading furniture prices: " + e.getMessage(), e);
            }
        });
    }


    public static void setPrice(String furnitureId, int price) {
        furniturePriceMap.put(furnitureId, price);

        CompletableFuture.runAsync(() -> {
            String query = "INSERT INTO furniture_prices (furnitureID, price) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE price = ?";
            try (Connection connection = SQLManager.getInstance().getConnection();
                 PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, furnitureId);
                ps.setInt(2, price);
                ps.setInt(3, price);
                ps.executeUpdate();
            } catch (SQLException e) {
                YellowMCCoreV2.getPlugin(YellowMCCoreV2.class)
                        .getLogger().log(Level.SEVERE, "Error setting furniture price: " + e.getMessage(), e);
            }
        });
    }

    public static void startRefreshRunner() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                YellowMCCoreV2.getPlugin(YellowMCCoreV2.class),
                () -> {
                    try {
                        FurnitureShopModule.getFurnitureSpawner().startGenerationTaskForAllRooms();
                    } catch (Exception e) {
                        YellowMCCoreV2.getPlugin(YellowMCCoreV2.class)
                                .getLogger().log(Level.SEVERE, "Error refreshing furniture prices: " + e.getMessage(), e);
                    }
                },
                0L,
                20L * 60 * 60 // Refresh every 60 minutes
        );
    }

    public static Map<String, Integer> getFurniturePriceMap() {
        return furniturePriceMap;
    }
}
