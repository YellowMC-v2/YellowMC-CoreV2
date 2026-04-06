package de.emn4tor.modules.smp.homes;

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.SQLManager;
import de.emn4tor.modules.smp.homes.commands.*;
import de.emn4tor.modules.smp.homes.listeners.HomeInventoryListener;
import de.emn4tor.modules.smp.homes.repository.HomeRepository;
import de.emn4tor.modules.smp.homes.services.HomeService;

@ModuleInfo(name = "HomeModule", server = "smp")
public class HomeModule implements Module {
    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        var sqlManager = SQLManager.getInstance();
        var homeRepository = new HomeRepository(sqlManager);

        homeRepository.createTable();

        var homeService = new HomeService(homeRepository);

        plugin.getServer().getPluginManager().registerEvents(new HomeInventoryListener(homeService), plugin);
        plugin.registerCommand("home", new HomeCommand(homeService));
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {

    }
}
