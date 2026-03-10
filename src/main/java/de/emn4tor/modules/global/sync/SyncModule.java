package de.emn4tor.modules.global.sync;

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.global.sync.listener.JoinSyncListener;
import de.emn4tor.modules.global.sync.listener.SyncFreezeListener;
import de.emn4tor.modules.global.sync.listener.TriggerSyncEvent;
import de.emn4tor.modules.global.sync.task.SyncTask;

@ModuleInfo(name= "SyncModule")
public class SyncModule implements Module {
    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        plugin.getServer().getPluginManager().registerEvents(new JoinSyncListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new SyncFreezeListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new TriggerSyncEvent(), plugin);

        SyncTask.startSyncTask();
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {

    }
}
