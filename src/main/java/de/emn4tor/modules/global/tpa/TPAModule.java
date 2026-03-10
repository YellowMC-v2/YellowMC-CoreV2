package de.emn4tor.modules.global.tpa;

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.global.tpa.api.RandomTeleportAPI;
import de.emn4tor.modules.global.tpa.api.TeleportAPI;
import de.emn4tor.modules.global.tpa.commands.RTPCommand;
import de.emn4tor.modules.global.tpa.listener.RTPInventoryListener;

@ModuleInfo(name="TPA-Module")
public class TPAModule implements Module {
    private TeleportAPI teleportAPI;
    private RandomTeleportAPI randomTeleportAPI;
    private RTPCommand rtpCommand;

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        this.teleportAPI = new TeleportAPI(plugin);
        this.randomTeleportAPI = new RandomTeleportAPI(plugin, this.teleportAPI);
        this.rtpCommand = new RTPCommand(this.teleportAPI);

        plugin.getServer().getPluginManager().registerEvents(new RTPInventoryListener(this.randomTeleportAPI), plugin);
        plugin.registerCommand("rtp", this.rtpCommand);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {

    }
}
