package de.emn4tor.modules.furnitureshop;

import de.emn4tor.Module;
import de.emn4tor.YellowMCCoreV2;

public class FurnitureShopModule implements Module {

    private static FurnitureShopModule instance;
    private FurnitureSpawner furnitureSpawner;

    @Override
    public String getName() {
        return "FurnitureShopModule";
    }

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        instance = this;
        furnitureSpawner = new FurnitureSpawner(YellowMCCoreV2.getInstance());
        plugin.getServer().getPluginManager().registerEvents(new FurnitureHoverListener(), plugin);
        plugin.getCommand("debugfurniture").setExecutor(new DebugCMD());
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {
        instance = null;
    }

    public static FurnitureSpawner getFurnitureSpawner() {
        return instance != null ? instance.furnitureSpawner : null;
    }
}
