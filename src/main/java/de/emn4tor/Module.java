package de.emn4tor;

/*
 *  @author: Emn4tor
 *  @created: 16.08.2025
 */

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents a modular feature for the plugin.
 * Each module can be independently enabled or disabled.
 */
public interface Module {

    /**
     * Returns the name of the module.
     *
     * @return module name
     */
    String getName();

    /**
     * Called when the module is enabled.
     * Use this to register commands, events, or initialize resources.
     *
     * @param plugin the main plugin instance
     */
    void onEnable(YellowMCCoreV2 plugin);

    /**
     * Called when the module is disabled.
     * Use this to clean up resources, unregister listeners, etc.
     *
     * @param plugin the main plugin instance
     */
    void onDisable(YellowMCCoreV2 plugin);
}