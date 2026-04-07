package de.emn4tor.modules.smp.claimsystem;

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.SQLManager;
import de.emn4tor.modules.smp.claimsystem.commands.ClaimCommand;
import de.emn4tor.modules.smp.claimsystem.listener.ChunkEnterListener;
import de.emn4tor.modules.smp.claimsystem.listener.ProhibitedActionsListener;
import de.emn4tor.modules.smp.claimsystem.logic.ClaimManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;

@ModuleInfo(name="ClaimsModule", server = "smp")
public class ClaimsModule implements Module {
    private static ClaimManager claimManager;
    YellowMCCoreV2 plugin;

    /**
     * Returns the singleton ClaimManager instance used throughout the plugin.
     *
     * @return the active ClaimManager
     */
    public static ClaimManager getClaimManager() {
        return claimManager;
    }

    /**
     * Called when the plugin is enabled.
     * <p>
     * Initializes the ClaimManager, registers event listeners for prohibited
     * actions, and sets up the command executors.
     */
    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        plugin = YellowMCCoreV2.getInstance();
        FileConfiguration config = plugin.getConfig();

        SQLManager.init(config);
        // Initialize the claim manager
        claimManager = new ClaimManager(plugin);

        // Register event listener for block restrictions
        plugin.getServer().getPluginManager().registerEvents(new ProhibitedActionsListener(claimManager), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ChunkEnterListener(claimManager), plugin);
        // Register /claim command executor
        plugin.getCommand("claim").setExecutor(new ClaimCommand(claimManager));
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {

    }
}
