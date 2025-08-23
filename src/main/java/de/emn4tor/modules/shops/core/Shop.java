package de.emn4tor.modules.shops.core;

/*
 *  @author: Emn4tor
 *  @created: 24.04.2025
 *  @updated: 22.05.2025
 */

import java.util.List;

public class Shop {
    private final String name;
    private final List<ShopItem> items;
    private final ShopType type;

    public Shop(String name, List<ShopItem> items) {
        this(name, items, ShopType.DEFAULT);
    }

    public Shop(String name, List<ShopItem> items, ShopType type) {
        this.name = name;
        this.items = items;
        this.type = type;
    }

    public String getName() { return name; }
    public List<ShopItem> getItems() { return items; }
    public ShopType getType() { return type; }
}