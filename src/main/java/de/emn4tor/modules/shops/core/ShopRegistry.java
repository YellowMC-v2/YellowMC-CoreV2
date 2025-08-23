package de.emn4tor.modules.shops.core;

/*
 *  @author: Emn4tor
 *  @created: 22.05.2025
 */

import java.util.HashMap;
import java.util.Map;

public class ShopRegistry {
    private final Map<String, Shop> shops = new HashMap<>();

    public void registerShop(String id, Shop shop) {
        shops.put(id, shop);
    }

    public Shop getShop(String id) {
        return shops.get(id);
    }

    public Map<String, Shop> getAllShops() {
        return new HashMap<>(shops);
    }
}