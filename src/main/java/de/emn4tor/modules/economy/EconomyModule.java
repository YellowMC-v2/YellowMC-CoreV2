package de.emn4tor.modules.economy;

/*
 *  @author: Emn4tor
 *  @created: 19.08.2025
 */

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.economy.coins.api.EconomyManager;
import de.emn4tor.modules.economy.coins.commands.BalanceCommand;
import de.emn4tor.modules.economy.coins.commands.PayCommand;
import de.emn4tor.modules.economy.rubies.RubiesCommand;
import de.emn4tor.modules.economy.rubies.RubyHandler;

@ModuleInfo(name = "EconomyModule", priority = 1)
public class EconomyModule implements Module {
    private static EconomyManager economyManager;

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        economyManager = new EconomyManager(plugin);
        economyManager.initialize();
        plugin.getCommand("balance").setExecutor(new BalanceCommand(economyManager));
        plugin.getCommand("pay").setExecutor(new PayCommand(economyManager, YellowMCCoreV2.getRedisManager()));
        plugin.getCommand("ecoadmin").setExecutor(new AdminCommand(economyManager));

        //Rubies
        RubyHandler.init(plugin);
        RubyHandler.initialize();
        plugin.getCommand("rubies").setExecutor(new RubiesCommand());
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {
    }

    public static EconomyManager getEconomyManager() {
        return economyManager;
    }


}