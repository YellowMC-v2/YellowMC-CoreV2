package de.emn4tor.modules.global.scoreboard;

/*
 * @author: Emn4tor
 * @created: 21.08.2025
 */

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import org.bukkit.Bukkit;

@ModuleInfo(name = "ScoreboardModule", priority = 90)
public class ScoreboardModule implements Module {

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        var coinService = YellowMCCoreV2.getCoinService();

        if (coinService == null) {
            plugin.getLogger().severe("ScoreboardModule: CoinService is null! Disabling module...");
            return;
        }

        var variableManager = new VariableManager(coinService);
        var scoreboardManager = new ScoreboardManager(plugin, variableManager);

        Bukkit.getPluginManager().registerEvents(scoreboardManager, plugin);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (var player : Bukkit.getOnlinePlayers()) {
                scoreboardManager.updateScoreBoard(player);
            }
        }, 20L, 40L);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {
    }
}