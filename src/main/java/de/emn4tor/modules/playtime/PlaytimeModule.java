package de.emn4tor.modules.playtime;

/*
 *  @author: Emn4tor
 *  @created: 18.08.2025
 */

import de.emn4tor.Module;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.SQLManager;

public class PlaytimeModule implements Module {
    private PlaytimeManager playTimeManager;
    private PlaytimeCommand commandHandler;
    private PlaytimeConnectionListener connectionListener;

    @Override
    public String getName() {
        return "PlaytimeModule";
    }

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        try {
            SQLManager.init(plugin.getConfig());
            playTimeManager = new PlaytimeManager(plugin);
            plugin.getLogger().info("Database successfully initialized!");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize SQLManager: " + e.getMessage());
            e.printStackTrace();
            plugin.getLogger().severe("Database initialization failed! Disabling plugin...");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }
        commandHandler = new PlaytimeCommand(plugin, playTimeManager);
        plugin.getCommand("playtime").setExecutor(commandHandler);
        connectionListener = new PlaytimeConnectionListener(plugin, playTimeManager);

        if (playTimeManager != null) {
            PlaytimeAPI.setPlayTimeManager(playTimeManager);
            plugin.getLogger().info("PlayTime plugin has been enabled!");
        }
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {
        if (playTimeManager != null) {
            plugin.getServer().getOnlinePlayers().forEach(player ->
                    playTimeManager.updatePlayTime(player.getUniqueId())
            );
        }

        // Close SQL pool
        SQLManager.getInstance().close();

        plugin.getLogger().info("PlayTime plugin has been disabled!");
    }

    public PlaytimeManager getPlayTimeManager() {
        return playTimeManager;
    }
}