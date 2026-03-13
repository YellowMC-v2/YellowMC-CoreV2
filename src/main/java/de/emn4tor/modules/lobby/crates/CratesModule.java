package de.emn4tor.modules.lobby.crates;

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.lobby.crates.commands.CratesCommand;
import de.emn4tor.modules.lobby.crates.registry.CrateManager;
import de.emn4tor.modules.lobby.crates.registry.CrateRegistry;
import de.emn4tor.modules.lobby.crates.listener.CrateInteractListener; // Don't forget this
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@ModuleInfo(name = "CratesModule", server = "lobby")
public class CratesModule implements Module {
    private CrateRegistry registry;
    private CrateManager crateManager;

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        this.registry = new CrateRegistry();
        this.crateManager = new CrateManager();

        registry.loadAll();

        int crateCount = registry.getAllCrates().size();
        plugin.getLogger().info("Successfully loaded " + crateCount + " crates.");

        plugin.getServer().getPluginManager().registerEvents(new CrateInteractListener(registry, crateManager), plugin);
        //plugin.getCommand("crates").setExecutor(new CratesCommand(this));
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {
    }

    public void reload() {
        if (registry != null) {
            registry.loadAll();
        }
    }
}