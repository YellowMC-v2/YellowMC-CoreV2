package de.emn4tor.modules.global.daily;

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import org.bukkit.Bukkit;

@ModuleInfo(name = "DailyModule")
public class DailyModule implements Module {

    private DailyManager dailyManager;

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        var rankGetter = new RankGetter();

        this.dailyManager = new DailyManager(rankGetter, YellowMCCoreV2.getCoinService());

        Bukkit.getPluginManager().registerEvents(new DailyListener(this.dailyManager), plugin);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {
    }
}