package de.emn4tor.modules.redeemables;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;

@ModuleInfo(name = "RedeemablesModule")
public class RedeemableModule implements Module {

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        plugin.getCommand("getredeemables").setExecutor(new GetRedeemableCommand());
        plugin.getServer().getPluginManager().registerEvents(new RedeemListener(), plugin);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {

    }
}
