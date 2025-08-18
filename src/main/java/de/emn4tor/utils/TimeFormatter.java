package de.emn4tor.utils;

/*
 *  @author: Emn4tor
 *  @created: 18.08.2025
 */

import java.util.concurrent.TimeUnit;

/**
 * Utility class for formatting time in milliseconds to a human-readable format
 */
public class TimeFormatter {

    /**
     * Formats time in milliseconds to a human-readable string
     *
     * @param timeInMillis Time in milliseconds
     * @return Formatted time string (e.g., "2d 5h 30m 15s")
     */
    public static String format(long timeInMillis) {
        long days = TimeUnit.MILLISECONDS.toDays(timeInMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60;

        StringBuilder builder = new StringBuilder();

        if (days > 0) {
            builder.append(days).append("d ");
        }

        if (hours > 0 || days > 0) {
            builder.append(hours).append("h ");
        }

        if (minutes > 0 || hours > 0 || days > 0) {
            builder.append(minutes).append("m ");
        }

        builder.append(seconds).append("s");

        return builder.toString();
    }
}
