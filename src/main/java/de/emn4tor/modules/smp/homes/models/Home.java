package de.emn4tor.modules.smp.homes.models;

import de.emn4tor.YellowMCCoreV2;
import lombok.Builder;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

@Builder
public record Home(UUID uuid, String serverID, int homeNumber, String worldName, double x, double y, double z, float yaw, float pitch) {
    public boolean isLocal() {
        return this.serverID.equalsIgnoreCase(YellowMCCoreV2.getServerName());
    }

    public Location toBukkitLocation() {
        var world = Bukkit.getWorld(worldName);

        if (world == null) return null;

        return new Location(world, x, y, z, yaw, pitch);
    }
}