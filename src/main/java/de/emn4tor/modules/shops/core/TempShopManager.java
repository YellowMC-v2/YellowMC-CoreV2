package de.emn4tor.modules.shops.core;

/*
 *  @author: Emn4tor
 *  @created: 22.05.2025
 */

import de.emn4tor.modules.shops.ShopLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TempShopManager {
    private final Plugin plugin;
    private final ShopLoader shopLoader;
    private final String tempShopTable;
    private final String tempShopName;
    private Shop tempShop;
    private final long refreshInterval;
    private int taskId = -1;

    public TempShopManager(Plugin plugin, ShopLoader shopLoader, String tempShopTable, String tempShopName) {
        this.plugin = plugin;
        this.shopLoader = shopLoader;
        this.tempShopTable = tempShopTable;
        this.tempShopName = tempShopName;
        this.refreshInterval = TimeUnit.HOURS.toSeconds(12);

        // Initial load
        refreshTempShop();

        // Schedule periodic refresh
        startRefreshTask();
    }

    public Shop getTempShop() {
        if (tempShop == null) {
            refreshTempShop();
        }
        return tempShop;
    }

    public void refreshTempShop() {
        // Load all items from the temp shop table
        List<ShopItem> allItems = shopLoader.loadTempShopItems(tempShopTable);

        // Select 3 random items
        List<ShopItem> selectedItems = selectRandomItems(allItems, 3);

        // Create a new temp shop with the selected items
        tempShop = new Shop(tempShopName, selectedItems, ShopType.TEMP);
    }

    private List<ShopItem> selectRandomItems(List<ShopItem> allItems, int count) {
        List<ShopItem> result = new ArrayList<>();
        if (allItems.size() <= count) {
            return new ArrayList<>(allItems);
        }

        Random random = new Random();
        List<Integer> indices = new ArrayList<>();

        while (indices.size() < count) {
            int index = random.nextInt(allItems.size());
            if (!indices.contains(index)) {
                indices.add(index);
                result.add(allItems.get(index));
            }
        }

        return result;
    }

    private void startRefreshTask() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
                this::refreshTempShop,
                refreshInterval * 20, // Initial delay (in ticks)
                refreshInterval * 20  // Repeat interval (in ticks)
        );
    }

    public void shutdown() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
}