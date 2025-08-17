package de.emn4tor;

/*
 *  @author: Emn4tor
 *  @created: 24.07.2025
 */

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public void registerModule(Module module) {
        modules.add(module);
    }

    public void enableModules(YellowMCCoreV2 plugin) {
        for (Module module : modules) {
            try {
                module.onEnable(plugin);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to enable module: " + module.getName() + " - " + e.getMessage());
                continue; // Skip this module and continue with the next one
            }

            plugin.getLogger().info("Enabled module: " + module.getName());
        }
    }

    public void disableModules(YellowMCCoreV2 plugin) {
        for (Module module : modules) {
            try {
                module.onDisable(plugin);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to disable module: " + module.getName() + " - " + e.getMessage());
                continue;
            }
            plugin.getLogger().info("Disabled module: " + module.getName());
        }
    }

    public List<Module> getModules() {
        return modules;
    }

}
