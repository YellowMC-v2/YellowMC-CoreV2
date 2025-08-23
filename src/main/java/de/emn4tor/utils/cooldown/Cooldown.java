package de.emn4tor.utils.cooldown;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

public class Cooldown {
    private final String uuid;
    private final String command;
    private final long cooldownTime; //in milliseconds
    private final long lastUsed;

    public Cooldown(String uuid, String command, long cooldownTime, long lastUsed) {
        this.uuid = uuid;
        this.command = command;
        this.cooldownTime = cooldownTime;
        this.lastUsed = lastUsed;
    }

    public String getUuid() {
        return uuid;
    }

    public String getCommand() {
        return command;
    }

    public long getCooldownTime() {
        return cooldownTime;
    }

    public long getLastUsed() {
        return lastUsed;
    }

    public boolean isOnCooldown() {
        return System.currentTimeMillis() - lastUsed < cooldownTime;
    }

    public long getRemainingTime() {
        long remaining = cooldownTime - (System.currentTimeMillis() - lastUsed);
        return Math.max(remaining, 0); // Ensure it doesn't return negative values
    }
}
