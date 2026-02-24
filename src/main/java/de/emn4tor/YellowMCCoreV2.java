package de.emn4tor;

import de.emn4tor.api.LocaleService;
import de.emn4tor.api.MessageService;
import de.emn4tor.api.TranslationService;
import de.emn4tor.commons.JoinListener;
import de.emn4tor.config.ConfigLoader;
import de.emn4tor.data.RedisManager;
import de.emn4tor.data.SQLManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class YellowMCCoreV2 extends JavaPlugin {

    private ModuleManager moduleManager;
    private RedisManager redisManager;

    private MessageService messageService;
    TranslationService translationService;
    LocaleService localeService;

    @Override
    public void onEnable() {
        translationService = Bukkit.getServicesManager().load(TranslationService.class);
        localeService = Bukkit.getServicesManager().load(LocaleService.class);
        messageService = Bukkit.getServicesManager().load(MessageService.class);

        if (translationService == null || localeService == null) {
            getLogger().severe("Failed to load TranslationService or LocaleService.");
            return;
        }

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

        //Initialize RedisManager
        redisManager = RedisManager.getInstance();
        redisManager.connect(getConfig());

        //Register modules
        moduleManager = new ModuleManager();

        moduleManager.discoverModules();
        moduleManager.enableModules(this);

        Bukkit.getServer().getPluginManager().registerEvents(new JoinListener(), this);
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

    public static RedisManager getRedisManager() {
        return getInstance().redisManager;
    }

    public static MessageService getMessageService() {return getInstance().messageService;}

    public static TranslationService getTranslationService() {
        return getInstance().translationService;
    }

    public static LocaleService getLocaleService() {
        return getInstance().localeService;
    }
}
