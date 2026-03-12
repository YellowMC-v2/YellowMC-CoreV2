package de.emn4tor;

import de.emn4tor.api.LocaleService;
import de.emn4tor.api.MessageService;
import de.emn4tor.api.TranslationService;
import de.emn4tor.api.player.SyncService;
import de.emn4tor.commons.JoinListener;
import de.emn4tor.config.ConfigLoader;
import de.emn4tor.data.RedisManager;
import de.emn4tor.data.SQLManager;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class YellowMCCoreV2 extends JavaPlugin {

    @Getter private static YellowMCCoreV2 instance;

    @Getter private LuckPerms luckPerms;

    @Getter private ModuleManager moduleManager;
    @Getter private RedisManager redisManager;
    @Getter private MessageService messageService;
    @Getter private TranslationService translationService;
    @Getter private LocaleService localeService;
    @Getter private SyncService syncService;

    @Override
    public void onEnable() {
        instance = this;

        this.luckPerms = LuckPermsProvider.get();

        ConfigLoader.load();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (!loadServices()) return;
        if (!initSQL()) return;

        initRedis();
        initModules();

        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        getLogger().info("YellowMC Core enabled successfully.");
    }

    @Override
    public void onDisable() {
        if (moduleManager != null) {
            moduleManager.disableModules(this);
        }
        SQLManager.getInstance().close();
        getLogger().info("YellowMC Core disabled.");
    }

    private boolean loadServices() {
        translationService = Bukkit.getServicesManager().load(TranslationService.class);
        localeService      = Bukkit.getServicesManager().load(LocaleService.class);
        messageService     = Bukkit.getServicesManager().load(MessageService.class);
        syncService        = Bukkit.getServicesManager().load(SyncService.class);

        if (translationService == null || localeService == null) {
            getLogger().severe("Failed to load TranslationService or LocaleService. Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }

    private boolean initSQL() {
        getLogger().info("Initializing SQLManager...");
        try {
            SQLManager.init(getConfig());
            getLogger().info("SQLManager initialized successfully.");
            return true;
        } catch (Exception e) {
            getLogger().severe("Failed to initialize SQLManager: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
    }

    private void initRedis() {
        redisManager = RedisManager.getInstance();
        redisManager.connect(getConfig());
    }

    private void initModules() {
        moduleManager = new ModuleManager();
        moduleManager.discoverModules();
        moduleManager.enableModules(this);
    }

    public static String getServerName() {
        return getInstance().getConfig().getString("server-name");
    }
}