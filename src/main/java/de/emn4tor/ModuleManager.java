package de.emn4tor;

/*
 *  @author: Emn4tor
 *  @created: 24.07.2025
 */


import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ModuleManager {

    private String serverName = YellowMCCoreV2.getInstance().getConfig().getString("server-name");
    private final List<Module> modules = new ArrayList<>();

    public void discoverModules() {
        // Scan for classesw with @ModuleInfo

        Reflections reflections = new Reflections("de.emn4tor.modules");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(ModuleInfo.class);

        List<Class<?>> sortedClasses = annotated.stream()
                .filter(Module.class::isAssignableFrom) // Ensure they implement Module
                .sorted(Comparator.comparingInt(clazz -> clazz.getAnnotation(ModuleInfo.class).priority()))
                .toList();

        for (Class<?> clazz : sortedClasses) {
            try {
                Module module = (Module) clazz.getDeclaredConstructor().newInstance();
                registerModule(module);

                // Optional: Log the priority for debugging
                int prio = clazz.getAnnotation(ModuleInfo.class).priority();
                Bukkit.getLogger().info("[ModuleManager] Discovered " + module.getName() + " (Prio: " + prio + ")");
            } catch (Exception e) {
                System.err.println("Could not load module: " + clazz.getSimpleName());
                e.printStackTrace();
            }
        }
    }

    public void registerModule(Module module) {
        modules.add(module);
    }

    public void enableModules(YellowMCCoreV2 plugin) {
        for (Module module : modules) {
            String requiredServer = module.getServerName();

            if (requiredServer != null && !requiredServer.equalsIgnoreCase(serverName)) {
                plugin.getLogger().info("Module " + module.getName() + " skipped (Server mismatch: " + requiredServer + ")");
                continue;
            }

            try {
                module.onEnable(plugin);
                plugin.getLogger().info("Enabled module: " + module.getName());
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to enable module: " + module.getName() + " - " + e.getMessage());
            }
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
