package de.emn4tor.modules.global.economy.coins.api.repositories;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface CoinRepository {
    void setupRepository();
    double findCoinsByUuid(@NotNull UUID uuid);
    void addCoinsByUuid(@NotNull UUID uuid, double coins);
    void setCoinsByUuid(@NotNull UUID uuid, double coins);
    void removeCoinsByUuid(@NotNull UUID uuid, double coins);
}
