package de.emn4tor.modules.furnitureshop;

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import org.bukkit.Bukkit;


@ModuleInfo(name = "FurnitureShopModule", server = "lobby")
public class FurnitureShopModule implements Module {
    private static FurnitureShopModule instance;
    private FurnitureSpawner furnitureSpawner;

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        if(plugin.getConfig().getBoolean("furniture.enabled")) {
            FurnitureShopManager manager = new FurnitureShopManager();
            instance = this;
            manager.createFurniturePricesTable();
            manager.loadFurniturePrices();
            furnitureSpawner = new FurnitureSpawner(YellowMCCoreV2.getInstance());
            plugin.getServer().getPluginManager().registerEvents(new FurnitureHoverListener(), plugin);
            plugin.getCommand("debugfurniture").setExecutor(new DebugCMD());
            plugin.getCommand("furnitureshop").setExecutor(new FurnitureShopCommand());
            FurnitureShopManager.startRefreshRunner();
        }
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {
        instance = null;
    }

    public static FurnitureSpawner getFurnitureSpawner() {
        return instance != null ? instance.furnitureSpawner : null;
    }
}
