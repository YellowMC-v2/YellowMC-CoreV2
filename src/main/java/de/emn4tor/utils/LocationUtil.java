package de.emn4tor.utils;

import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class LocationUtil {

    public static Location deserialize(String input) {
        if (input == null || input.isEmpty()) return null;

        String[] parts = input.split(",");

        if (parts.length < 4) return null;

        try {
            World world = Bukkit.getWorld(parts[0].trim());
            if (world == null) return null;

            double x = Double.parseDouble(parts[1].trim());
            double y = Double.parseDouble(parts[2].trim());
            double z = Double.parseDouble(parts[3].trim());

            if (parts.length >= 6) {
                float yaw = Float.parseFloat(parts[4].trim());
                float pitch = Float.parseFloat(parts[5].trim());
                return new Location(world, x, y, z, yaw, pitch);
            }

            return new Location(world, x, y, z);

        } catch (NumberFormatException e) {
            return null;
        }
    }
}
