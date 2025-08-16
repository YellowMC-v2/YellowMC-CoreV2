package de.emn4tor;

/*
 *  @author: Emn4tor
 *  @created: 24.07.2025
 */

import de.emn4tor.config.ConfigLoader;
import de.emn4tor.data.SQLManager;

public class PluginBootstrap {
    private final YellowMCCoreV2 plugin;

    public PluginBootstrap(YellowMCCoreV2 plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        //Load configuration
        plugin.getLogger().info("Loading configuration...");
        ConfigLoader.load();
        plugin.getLogger().info("Configuration loaded successfully.");
        //Initialize HikariCP SQLManager
        plugin.getLogger().info("Initializing SQLManager...");
        SQLManager.init(plugin.getConfig());
        plugin.getLogger().info("SQLManager initialized successfully.");
        printInfo();
    }

    public void disable() {
        SQLManager.getInstance().close();
    }

    private void printInfo() {
        plugin.getLogger().info("Core plugin initialized...");
    }
}
