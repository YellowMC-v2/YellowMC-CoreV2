package de.emn4tor.modules.scoreboard;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.economy.EconomyModule;
import de.emn4tor.modules.economy.coins.api.EconomyHandler;
import de.emn4tor.modules.economy.coins.api.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@ModuleInfo(name = "ScoreboardModule", priority = 90)
public class ScoreboardModule implements Module {

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        EconomyManager ecoManager = EconomyModule.getEconomyManager();

        if (ecoManager == null) {
            plugin.getLogger().severe("ScoreboardModule: EconomyManager is null! Disabling module...");
            return;
        }

        ScoreboardManager boards = new ScoreboardManager(plugin, new VariableManager(EconomyModule.getEconomyManager()));
        plugin.getServer().getPluginManager().registerEvents(boards, plugin);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Update scoreboards every 2 seconds
            for (Player p : Bukkit.getOnlinePlayers()) {
                boards.updateScoreBoard(p);
            }
        }, 20L, 40L);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {

    }
}
