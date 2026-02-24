package de.emn4tor.modules.global.daily;

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;

@ModuleInfo(name = "DailyModule")
public class DailyModule implements Module {
    DailyManager dailyManager;
    RankGetter rankGetter;

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        RankGetter rankGetter = new RankGetter();
        dailyManager = new DailyManager(rankGetter);
        plugin.getServer().getPluginManager().registerEvents(new DailyListener(dailyManager), plugin);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {

    }
}
