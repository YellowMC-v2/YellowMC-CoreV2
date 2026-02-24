package de.emn4tor.modules.global.scoreboard;

/*
 *  @author: Emn4tor
 *  @created: 09.04.2025
 */

import de.emn4tor.modules.global.economy.coins.api.EconomyManager;
import de.emn4tor.modules.global.playtime.PlaytimeAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VariableManager {
    private final EconomyManager economyManager;

    public VariableManager(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }


    private final Map<UUID, Integer> playtime = new HashMap<>();
    private final Map<UUID, Integer> kills = new HashMap<>();
    private final Map<UUID, Integer> deaths = new HashMap<>();




    public CompletableFuture<Integer> getPlaytime(UUID id) {
        return CompletableFuture.supplyAsync(() -> {
            long playtime = PlaytimeAPI.getCurrentPlayTime(id);
            return (int) TimeUnit.MILLISECONDS.toHours(playtime);
        });
    }


    public int getRubies(UUID id) {
        long playtime = PlaytimeAPI.getCurrentPlayTime(id);
        return (int) TimeUnit.MILLISECONDS.toHours(playtime);
    }

    public CompletableFuture<Integer> getBalance(UUID id) {
        Player player = Bukkit.getPlayer(id);
        if (player == null) {
            return CompletableFuture.completedFuture(0);
        }
        return economyManager.getCoins(player.getUniqueId());
    }



}
