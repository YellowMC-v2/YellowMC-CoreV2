package de.emn4tor.modules.global.scoreboard;

import de.emn4tor.modules.global.economy.coins.api.services.CoinService;
import de.emn4tor.modules.global.economy.rubies.RubyHandler;
import de.emn4tor.modules.global.playtime.PlaytimeAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VariableManager {

    private final CoinService coinService;

    private final Map<UUID, Integer> playtimeCache = new HashMap<>();
    private final Map<UUID, Integer> kills = new HashMap<>();
    private final Map<UUID, Integer> deaths = new HashMap<>();

    public VariableManager(CoinService coinService) {
        this.coinService = coinService;
    }

    public int getPlaytime(UUID id) {
        long playtime = PlaytimeAPI.getCurrentPlayTime(id);
        return (int) TimeUnit.MILLISECONDS.toHours(playtime);
    }

    public CompletableFuture<Integer> getRubiesAsync(UUID id) {
        return RubyHandler.getRubiesAsync(id);
    }

    public double getBalance(UUID uuid) {
        return this.coinService.getCoins(uuid);
    }
}