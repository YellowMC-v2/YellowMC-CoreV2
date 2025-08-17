package de.emn4tor;

import de.emn4tor.config.ConfigLoader;
import de.emn4tor.data.SQLManager;
import de.emn4tor.modules.TestModule.TestModule;
import org.bukkit.plugin.java.JavaPlugin;

public final class YellowMCCoreV2 extends JavaPlugin {

    private ModuleManager moduleManager;

    @Override
    public void onEnable() {
        //Load configuration
        getLogger().info("Loading configuration...");
        ConfigLoader.load();
        getLogger().info("Configuration loaded successfully.");
        //Initialize HikariCP SQLManager
        getLogger().info("Initializing SQLManager...");
        try {
            SQLManager.init(getConfig());
        }
        catch (Exception e) {
            for (int i = 0; i < 10; i++) {
                getLogger().severe("Failed to initialize SQLManager - PLUGIN WILL CRASH - ONLY FOR DEBUGGING " + e.getMessage());
            }
        }
        getLogger().info("SQLManager initialized successfully.");
        getLogger().info("Core plugin initialized...");

        //Register modules
        moduleManager = new ModuleManager();

        moduleManager.registerModule(new TestModule());

        moduleManager.enableModules(this);

    }

    @Override
    public void onDisable() {
        if (moduleManager != null) {
            moduleManager.disableModules(this);
        }
        SQLManager.getInstance().close();
    }

    public static YellowMCCoreV2 getInstance() {
        return JavaPlugin.getPlugin(YellowMCCoreV2.class);
    }
}
