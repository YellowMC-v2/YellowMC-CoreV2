package de.emn4tor.modules.global.economy;

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.global.economy.coins.commands.BalanceCommand;
import de.emn4tor.modules.global.economy.coins.commands.PayCommand;
import de.emn4tor.modules.global.economy.rubies.RubiesCommand;
import de.emn4tor.modules.global.economy.rubies.RubyHandler;

@ModuleInfo(name = "EconomyModule", priority = 1)
public class EconomyModule implements Module {

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        var coinService = YellowMCCoreV2.getCoinService();

        plugin.getCommand("balance").setExecutor(new BalanceCommand(coinService));
        plugin.getCommand("pay").setExecutor(new PayCommand(coinService));
        plugin.getCommand("ecoadmin").setExecutor(new AdminCommand(coinService));

        RubyHandler.init(plugin);
        RubyHandler.initialize();
        plugin.getCommand("rubies").setExecutor(new RubiesCommand());
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {
    }
}