package de.emn4tor.modules.furnitureshop;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.SQLManager;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class FurnitureShopManager {

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




    public static void setPrice(String furnitureId, int price) {
    }

    private static void saveToDatabase(String furnitureId, int price) {
    }

}
