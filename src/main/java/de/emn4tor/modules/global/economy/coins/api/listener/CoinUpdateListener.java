package de.emn4tor.modules.global.economy.coins.api.listener;

import de.emn4tor.modules.global.economy.coins.api.services.CoinService;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class CoinUpdateListener extends JedisPubSub {

    private final CoinService coinService;
    private final String localServerId;

    public CoinUpdateListener(CoinService coinService, String localServerId) {
        this.coinService = coinService;
        this.localServerId = localServerId;
    }

    @Override
    public void onMessage(String channel, String message) {
        var parts = message.split(":");
        if (parts.length < 4) return;

        var uuid = UUID.fromString(parts[0]);
        var action = parts[1];
        var amount = Double.parseDouble(parts[2]);
        var originServerId = parts[3];

        if (!originServerId.equals(this.localServerId)) {
            this.coinService.handleExternalUpdate(uuid, action, amount);
        }
    }
}