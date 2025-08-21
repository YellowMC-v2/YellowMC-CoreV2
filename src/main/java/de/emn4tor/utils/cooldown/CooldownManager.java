package de.emn4tor.utils.cooldown;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.data.RedisManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {
    private final RedisManager redis;

    private final Map<String, Cooldown> cooldowns = new ConcurrentHashMap<>();

    public CooldownManager(RedisManager redis) {
        this.redis = redis;
    }

    public void setCooldown(String uuid, String command, int ms) {
        String key = "cooldown:" + uuid + ":" + command;
        redis.setTemporary(key, String.valueOf(System.currentTimeMillis() + ms), ms / 1000);
    }

    public boolean hasCooldown(String uuid, String command) {
        if ("true".equals(redis.get("cooldown_bypass:" + uuid))) {
            return false;
        }

        String key = "cooldown:" + uuid + ":" + command;
        String value = redis.get(key);
        if (value == null) return false;

        try {
            long expiresAt = Long.parseLong(value);
            if (System.currentTimeMillis() >= expiresAt) {
                redis.delete(key);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            redis.delete(key);
            return false;
        }
    }

    public long getRemainingCooldown(String uuid, String command) {
        String key = "cooldown:" + uuid + ":" + command;
        String value = redis.get(key);
        if (value == null) return 0;

        try {
            long expiresAt = Long.parseLong(value);
            long remaining = expiresAt - System.currentTimeMillis();
            return Math.max(0, remaining);
        } catch (NumberFormatException e) {
            redis.delete(key);
            return 0;
        }
    }

    public String getRemainingCooldownFormatted(String uuid, String command) {
        long millis = getRemainingCooldown(uuid, command);

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || sb.length() == 0) sb.append(seconds).append("s");

        return sb.toString().trim();
    }

}
