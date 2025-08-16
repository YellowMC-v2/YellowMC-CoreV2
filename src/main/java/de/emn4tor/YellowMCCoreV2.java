package de.emn4tor;

import de.emn4tor.data.SQLManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class YellowMCCoreV2 extends JavaPlugin {

    @Override
    public void onEnable() {
        new PluginBootstrap(this).enable();
    }

    @Override
    public void onDisable() {
        new PluginBootstrap(this).disable();
    }

    public static YellowMCCoreV2 getInstance() {
        return JavaPlugin.getPlugin(YellowMCCoreV2.class);
    }
}
