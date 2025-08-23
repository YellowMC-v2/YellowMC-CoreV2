package de.emn4tor.modules.scoreboard;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.Module;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.economy.EconomyModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ScoreboardModule implements Module {
    @Override
    public String getName() {
        return "ScoreboardModule";
    }

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
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
