package de.emn4tor.modules.global.economy.coins.api.repositories;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface CoinRepository {
    void setupRepository();
    int findCoinsByUuid(@NotNull UUID uuid);
    void addCoinsByUuid(@NotNull UUID uuid, int coins);
    void setCoinsByUuid(@NotNull UUID uuid, int coins);
    void removeCoinsByUuid(@NotNull UUID uuid, int coins);
}
