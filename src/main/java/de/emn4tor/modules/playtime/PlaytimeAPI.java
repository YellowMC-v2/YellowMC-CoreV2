package de.emn4tor.modules.playtime;

/*
 *  @author: Emn4tor
 *  @created: 18.08.2025
 */

import de.emn4tor.utils.TimeFormatter;

import java.util.UUID;

/**
 * API for accessing playtime data from other plugins
 */
public class PlaytimeAPI {
    private static PlaytimeManager playTimeManager;

    /**
     * Sets the PlaytimeManager instance
     *
     * @param manager The PlaytimeManager instance
     */
    public static void setPlayTimeManager(PlaytimeManager manager) {
        playTimeManager = manager;
    }

    /**
     * Gets a player's current playtime without updating the database
     * This is optimized for frequent calls (e.g., from scoreboards)
     *
     * @param uuid The player's UUID
     * @return The player's current playtime in milliseconds
     */
    public static long getCurrentPlayTime(UUID uuid) {
        if (playTimeManager == null) {
            throw new IllegalStateException("PlayTimeAPI not initialized");
        }
        return playTimeManager.getCurrentPlayTime(uuid);
    }

    /**
     * Gets a player's playtime and updates it in the database
     *
     * @param uuid The player's UUID
     * @return The player's total playtime in milliseconds
     */
    public static long getPlayTime(UUID uuid) {
        if (playTimeManager == null) {
            throw new IllegalStateException("PlayTimeAPI not initialized");
        }
        return playTimeManager.getPlayTime(uuid);
    }

    /**
     * Formats playtime in milliseconds to a human-readable string
     *
     * @param timeInMillis Time in milliseconds
     * @return Formatted time string (e.g., "2d 5h 30m 15s")
     */
    public static String formatPlayTime(long timeInMillis) {
        return TimeFormatter.format(timeInMillis);
    }
}
