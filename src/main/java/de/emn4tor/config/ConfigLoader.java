package de.emn4tor.config;

/*
 *  @author: Emn4tor
 *  @created: 24.07.2025
 */

import de.emn4tor.YellowMCCoreV2;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigLoader {
    public static void load() {
        YellowMCCoreV2 plugin = YellowMCCoreV2.getInstance();
        plugin.saveDefaultConfig();

        FileConfiguration config = plugin.getConfig();

        //File testConfig = new File(plugin.getDataFolder(), "test.yml");
        //if (!testConfig.exists()) plugin.saveResource("test.yml", false);
    }
}
