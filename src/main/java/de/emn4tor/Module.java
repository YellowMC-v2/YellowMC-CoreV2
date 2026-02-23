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
     * Retrieves the name of the module defined in the @ModuleInfo annotation.
     * If the annotation is missing, it falls back to the class name.
     *
     * @return the module name from metadata, or class name as fallback.
     */
    default String getName() {
        if (this.getClass().isAnnotationPresent(ModuleInfo.class)) {
            return this.getClass().getAnnotation(ModuleInfo.class).name();
        }
        return this.getClass().getSimpleName();
    }

    /**
     * Retrieves the required server name for this module from @ModuleInfo metadata.
     * If no server is specified or the annotation is missing, the module is
     * considered universal.
     *
     * @return the required server name, or null if it should run on all servers.
     */
    default String getServerName() {
        if (this.getClass().isAnnotationPresent(ModuleInfo.class)) {
            String server = this.getClass().getAnnotation(ModuleInfo.class).server();
            return server.isEmpty() ? null : server;
        }
        return null;
    }

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