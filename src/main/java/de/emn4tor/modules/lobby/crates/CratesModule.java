package de.emn4tor.modules.lobby.crates;

import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.lobby.crates.commands.CratesCommand;
import de.emn4tor.modules.lobby.crates.keys.CrateKeyRepository;
import de.emn4tor.modules.lobby.crates.listener.CrateAnimationGUIListener;
import de.emn4tor.modules.lobby.crates.registry.CrateManager;
import de.emn4tor.modules.lobby.crates.registry.CrateRegistry;
import de.emn4tor.modules.lobby.crates.listener.CrateInteractListener; // Don't forget this
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Getter
@NoArgsConstructor
@ModuleInfo(name = "CratesModule", server = "lobby")
public class CratesModule implements Module, Listener {
    @Getter private static CratesModule instance;

    private CrateRegistry registry;
    private CrateManager crateManager;
    private CrateKeyRepository keyRepository;
    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        instance = this;
        keyRepository = new CrateKeyRepository();
        this.crateManager = new CrateManager(keyRepository);




        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        CratesCommand cmd = new CratesCommand(this);
        var command = plugin.getCommand("crates");
        command.setExecutor(cmd);
        command.setTabCompleter(cmd);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {
    }

    @EventHandler
    public void ItemsLoadEvent(NexoItemsLoadedEvent event) {
        this.registry = new CrateRegistry();
        registry.loadAll();

        int crateCount = registry.getAllCrates().size();
        YellowMCCoreV2.getInstance().getLogger().info("Successfully loaded " + crateCount + " crates.");
        YellowMCCoreV2.getInstance().getServer().getPluginManager().registerEvents(new CrateInteractListener(registry, crateManager), YellowMCCoreV2.getInstance());
        YellowMCCoreV2.getInstance().getServer().getPluginManager().registerEvents(new CrateAnimationGUIListener(crateManager), YellowMCCoreV2.getInstance());

    }

    public void reload() {
        if (registry != null) {
            registry.loadAll();
        }
    }

}