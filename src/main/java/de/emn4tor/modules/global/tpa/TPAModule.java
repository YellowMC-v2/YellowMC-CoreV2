package de.emn4tor.modules.global.tpa;

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.global.tpa.api.RandomTeleportAPI;
import de.emn4tor.modules.global.tpa.api.TeleportAPI;
import de.emn4tor.modules.global.tpa.commands.RTPCommand;
import de.emn4tor.modules.global.tpa.commands.TPAAcceptCommand;
import de.emn4tor.modules.global.tpa.commands.TPACommand;
import de.emn4tor.modules.global.tpa.listener.RTPInventoryListener;
import de.emn4tor.modules.global.tpa.services.TPAService;

@ModuleInfo(name="TPA-Module")
public class TPAModule implements Module {
    private TeleportAPI teleportAPI;
    private RandomTeleportAPI randomTeleportAPI;

    private TPAService tpaService;
    private TPACommand tpaCommand;
    private TPAAcceptCommand tpaAcceptCommand;
    private RTPCommand rtpCommand;

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        this.teleportAPI = new TeleportAPI(plugin);
        this.tpaService = new TPAService(plugin, this.teleportAPI);
        this.randomTeleportAPI = new RandomTeleportAPI(plugin, this.teleportAPI);
        this.rtpCommand = new RTPCommand();

        this.tpaCommand = new TPACommand(this.tpaService);
        this.tpaAcceptCommand = new TPAAcceptCommand(this.tpaService);

        plugin.getServer().getPluginManager().registerEvents(new RTPInventoryListener(this.randomTeleportAPI), plugin);

        plugin.registerCommand("rtp", this.rtpCommand);
        plugin.registerCommand("tpa", this.tpaCommand);
        plugin.registerCommand("tpaaccept", this.tpaAcceptCommand);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {

    }
}
