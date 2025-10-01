package de.emn4tor.modules.furnitureshop;

import de.emn4tor.Module;
import de.emn4tor.YellowMCCoreV2;

public class FurnitureShopModule implements Module {
    @Override
    public String getName() {
        return "FurnitureShopModule";
    }

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        plugin.getServer().getPluginManager().registerEvents(new FurnitureHoverListener(), plugin);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {

    }
}
