package de.emn4tor.modules.global.economy.coins.api.services;

import de.emn4tor.data.RedisManager;
import de.emn4tor.modules.global.economy.coins.api.listener.CoinUpdateListener;
import de.emn4tor.modules.global.economy.coins.api.repositories.CoinRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class CoinService {

    private final CoinRepository coinRepository;
    private final RedisManager redisManager;
    private final String serverId;

    private static final String REDIS_PREFIX = "coins:";
    private static final String CHANNEL_UPDATE = "economy:coin_update";

    private final Set<UUID> dirtyPlayers = ConcurrentHashMap.newKeySet();

    public void init(JavaPlugin plugin) {
        this.redisManager.subscribe(CHANNEL_UPDATE, new CoinUpdateListener(this, this.serverId));
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveDirtyPlayersToDatabase, 100L, 6000L);
    }

    public int getCoins(@NotNull UUID uuid) {
        var key = REDIS_PREFIX + uuid.toString();
        var cachedValue = this.redisManager.get(key);

        if (cachedValue != null) return Integer.parseInt(cachedValue);

        var dbValue = this.coinRepository.findCoinsByUuid(uuid);
        var finalValue = dbValue;

        if (dbValue == -1) {
            finalValue = 100;

            this.coinRepository.setCoinsByUuid(uuid, finalValue);
        }

        this.redisManager.setTemporary(key, String.valueOf(finalValue), 3600);
        return finalValue;
    }

    public void addCoins(@NotNull UUID uuid, int amount) {
        if (amount <= 0) return;
        this.updateBalance(uuid, this.getCoins(uuid) + amount, "ADDED", amount);
    }

    public void removeCoins(@NotNull UUID uuid, int amount) {
        if (amount <= 0) return;
        this.updateBalance(uuid, Math.max(0, this.getCoins(uuid) - amount), "REMOVED", amount);
    }

    public void setCoins(@NotNull UUID uuid, int amount) {
        this.updateBalance(uuid, Math.max(0, amount), "SET", amount);
    }

    private void updateBalance(UUID uuid, int newTotal, String action, int change) {
        this.redisManager.setTemporary(REDIS_PREFIX + uuid.toString(), String.valueOf(newTotal), 3600);
        this.dirtyPlayers.add(uuid);
        this.redisManager.publish(CHANNEL_UPDATE, uuid + ":" + action + ":" + change + ":" + this.serverId);
    }

    public void handleExternalUpdate(UUID uuid, String action, int amount) {
        this.redisManager.delete(REDIS_PREFIX + uuid.toString());

        var player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        var message = switch (action) {
            case "ADDED" -> "+ " + amount;
            case "REMOVED" -> "- " + amount;
            case "SET" -> "neu" + amount;
            default -> "aktualisiert";
        };
        player.sendMessage(message);
    }

    public void saveDirtyPlayersToDatabase() {
        if (this.dirtyPlayers.isEmpty()) return;

        var toSave = new HashSet<>(this.dirtyPlayers);
        this.dirtyPlayers.clear();

        for (var uuid : toSave) {
            var currentBalance = this.getCoins(uuid);
            this.saveToDatabase(uuid, currentBalance);
        }
    }

    private void saveToDatabase(UUID uuid, int balance) {
        this.coinRepository.setCoinsByUuid(uuid, balance);
    }

    public void shutdown() {
        this.saveDirtyPlayersToDatabase();
    }
}