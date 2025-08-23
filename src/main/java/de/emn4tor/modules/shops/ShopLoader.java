package de.emn4tor.modules.shops;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import de.emn4tor.data.SQLManager;
import de.emn4tor.modules.shops.core.Shop;
import de.emn4tor.modules.shops.core.ShopItem;
import de.emn4tor.modules.shops.core.ShopType;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShopLoader {

    public Shop loadShop(String tableName, String displayName) {
        List<ShopItem> items = loadShopItems(tableName);
        return new Shop(displayName, items);
    }

    public Shop loadShop(String tableName, String displayName, ShopType type) {
        List<ShopItem> items = loadShopItems(tableName);
        return new Shop(displayName, items, type);
    }

    private List<ShopItem> loadShopItems(String tableName) {
        List<ShopItem> items = new ArrayList<>();

        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + tableName);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Material material = Material.valueOf(rs.getString("material"));
                String name = rs.getString("display_name");
                String loreJson = rs.getString("lore");
                int price = rs.getInt("price");
                int amount = rs.getInt("amount");

                int customModelData = 0;
                try {
                    customModelData = rs.getInt("custom_model_data");
                } catch (SQLException ignored) {
                    // Column doesn't exist
                }

                List<String> lore = new Gson().fromJson(loreJson, new TypeToken<List<String>>(){}.getType());

                items.add(new ShopItem(material, name, lore, price, amount, customModelData));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<ShopItem> loadTempShopItems(String tableName) {
        return loadShopItems(tableName);
    }
}