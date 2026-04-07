package de.emn4tor;

import de.emn4tor.api.LocaleService;
import de.emn4tor.api.MessageService;
import de.emn4tor.api.TranslationService;
import de.emn4tor.api.player.SyncService;
import de.emn4tor.commons.JoinListener;
import de.emn4tor.config.ConfigLoader;
import de.emn4tor.data.RedisManager;
import de.emn4tor.data.SQLManager;
import de.emn4tor.utils.bridge.NetworkBridge;
import de.emn4tor.modules.global.economy.coins.api.repositories.impl.MySQLCoinRepository;
import de.emn4tor.modules.global.economy.coins.api.services.CoinService;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public final class YellowMCCoreV2 extends JavaPlugin {

    @Getter private static YellowMCCoreV2 instance;
    @Getter private LuckPerms luckPerms;

    @Getter private static ModuleManager moduleManager;
    @Getter private static RedisManager redisManager;
    @Getter private static MessageService messageService;
    @Getter private static TranslationService translationService;
    @Getter private static LocaleService localeService;
    @Getter private static SyncService syncService;
    @Getter private static CoinService coinService;

    private NetworkBridge networkBridge;

    @Override
    public void onEnable() {
        translationService = Bukkit.getServicesManager().load(TranslationService.class);
        localeService = Bukkit.getServicesManager().load(LocaleService.class);
        messageService = Bukkit.getServicesManager().load(MessageService.class);
        this.networkBridge = new NetworkBridge(this);
        instance = this;

        this.luckPerms = LuckPermsProvider.get();

        ConfigLoader.load();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (!loadServices()) return;
        if (!initSQL()) return;

        initRedis();
        initEconomy();
        initModules();

        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);

        this.networkBridge.startRedisListener();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Bukkit.getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getLogger().info("YellowMC Core enabled successfully.");
    }

    @Override
    public void onDisable() {
        if (coinService != null) {
            coinService.shutdown();
        }

        if (moduleManager != null) {
            moduleManager.disableModules(this);
        }

        SQLManager.getInstance().close();

        if (redisManager != null) {
            redisManager.close();
        }

        getLogger().info("YellowMC Core disabled.");
    }

    private void initEconomy() {
        Connection connection = null;

        try {
            connection = SQLManager.getInstance().getConnection();
        } catch (SQLException exception) {
            this.getLogger().severe(exception.getMessage());
            return;
        }

        var coinRepository = new MySQLCoinRepository(connection);

        coinRepository.setupRepository();

        coinService = new CoinService(coinRepository, redisManager, getServerName());
        coinService.init(this);

        getLogger().info("Economy-Service initialized.");
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
        return getInstance().getConfig().getString("server-name", "unknown-server");
    }

    public NetworkBridge getNetworkBridge() {
        return networkBridge;
    }
}
}
