package de.emn4tor.modules.daily;

/*
 *  @author: Emn4tor
 *  @created: 12.06.2025
 */

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DailyListener implements Listener {
    private final DailyManager dailyManager;

    public DailyListener(DailyManager dailyManager) {
        this.dailyManager = dailyManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if(!dailyManager.isOnCooldown(event.getPlayer())){
            dailyManager.giveDailyReward(event.getPlayer());
        }
    }
}
