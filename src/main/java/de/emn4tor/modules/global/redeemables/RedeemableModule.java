package de.emn4tor.modules.global.redeemables;

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import org.bukkit.Bukkit;

@ModuleInfo(name = "RedeemablesModule")
public class RedeemableModule implements Module {

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        var getRedeemableCommand = plugin.getCommand("getredeemables");
        if (getRedeemableCommand != null) {
            getRedeemableCommand.setExecutor(new GetRedeemableCommand());
        }

        var coinService = YellowMCCoreV2.getCoinService();
        Bukkit.getPluginManager().registerEvents(new RedeemListener(coinService), plugin);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {
    }
}