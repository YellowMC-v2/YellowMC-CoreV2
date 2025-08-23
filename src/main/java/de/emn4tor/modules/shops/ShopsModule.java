package de.emn4tor.modules.shops;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.Module;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.shops.command.ShopCommand;
import de.emn4tor.modules.shops.command.ShopsCommand;
import de.emn4tor.modules.shops.core.Shop;
import de.emn4tor.modules.shops.core.ShopRegistry;
import de.emn4tor.modules.shops.core.ShopService;
import de.emn4tor.modules.shops.core.TempShopManager;
import de.emn4tor.modules.shops.gui.ShopListener;
import de.emn4tor.modules.shops.keyshop.KeyShopCommand;
import de.emn4tor.modules.shops.keyshop.KeyShopListener;

import java.util.Map;

public class ShopsModule implements Module {
    @Override
    public String getName() {
        return "ShopsModule";
    }

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        ShopLoader loader = new ShopLoader();
        ShopService shopService = new ShopService();
        ShopRegistry shopRegistry = new ShopRegistry();


        TempShopManager tempShopManager = new TempShopManager(plugin, loader, "temp_shop_items", "Daily Specials");
        shopRegistry.registerShop("temp", tempShopManager.getTempShop());

        Shop foodShop = loader.loadShop("shop_food", "Food Shop");
        shopRegistry.registerShop("food", foodShop);

        Shop woodShop = loader.loadShop("shop_wood", "Wood Shop");
        shopRegistry.registerShop("wood", woodShop);

        Shop plantShop = loader.loadShop("shop_plants", "Plant Shop");
        shopRegistry.registerShop("plants", plantShop);



        Map<String, Shop> shopMap = shopRegistry.getAllShops();


        plugin.getCommand("foodshop").setExecutor(new ShopCommand(foodShop));
        plugin.getCommand("woodshop").setExecutor(new ShopCommand(woodShop));
        plugin.getCommand("plantshop").setExecutor(new ShopCommand(plantShop));
        plugin.getCommand("shops").setExecutor(new ShopsCommand());
        plugin.getCommand("tempshop").setExecutor(new ShopCommand(tempShopManager.getTempShop()));
        plugin.getCommand("keyshop").setExecutor(new KeyShopCommand());

        plugin.getServer().getPluginManager().registerEvents(new ShopsCommand(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new KeyShopListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ShopListener(shopMap, shopService), plugin);


    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {

    }
}
