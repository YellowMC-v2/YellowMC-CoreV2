package de.emn4tor.modules.global.muzzle.bans;

/*
 *  @author: Emn4tor
 *  @created: 07.05.2025
 */

import de.emn4tor.YellowMCCoreV2;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginListener implements Listener {
    private final BanManager banManager;
    private final YellowMCCoreV2 plugin;

    public LoginListener(BanManager banManager, YellowMCCoreV2 plugin) {
        this.banManager = banManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event){
        String uuid = event.getPlayer().getUniqueId().toString();
        Bukkit.getLogger().info("Checking if player " + event.getPlayer().getName() + " is banned...");
        banManager.isBannedAsync(uuid);
    }
}
