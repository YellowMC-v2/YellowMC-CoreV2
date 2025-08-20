package de.emn4tor.modules.economy;

/*
 *  @author: Emn4tor
 *  @created: 19.08.2025
 */

import de.emn4tor.Module;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.economy.coins.api.EconomyManager;
import de.emn4tor.modules.economy.coins.commands.BalanceCommand;
import de.emn4tor.modules.economy.coins.commands.PayCommand;
import de.emn4tor.modules.economy.rubies.RubiesCommand;
import de.emn4tor.modules.economy.rubies.RubyHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EconomyModule implements Module {
    private static EconomyManager economyManager;

    @Override
    public String getName() {
        return "EconomyModule";
    }

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        economyManager = new EconomyManager(plugin);
        economyManager.initialize();
        plugin.getCommand("balance").setExecutor(new BalanceCommand(economyManager));
        plugin.getCommand("pay").setExecutor(new PayCommand(economyManager, YellowMCCoreV2.getRedisManager()));
        plugin.getCommand("ecoadmin").setExecutor(new AdminCommand(economyManager));

        //Rubies
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