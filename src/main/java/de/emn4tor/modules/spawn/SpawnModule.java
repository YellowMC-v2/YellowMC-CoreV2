package de.emn4tor.modules.spawn;

/*
 *  @author: Emn4tor
 *  @created: 23.08.2025
 */

import de.emn4tor.Module;
import de.emn4tor.YellowMCCoreV2;

public class SpawnModule implements Module {
    @Override
    public String getName() {
        return "SpawnModule";
    }


    @Override
    public String getServerName() {
        return "spawn";
    }

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        plugin.getCommand("spawn").setExecutor(new SpawnCommand(plugin));
        plugin.getServer().getPluginManager().registerEvents(new SpawnListener(), plugin);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {

    }
}
