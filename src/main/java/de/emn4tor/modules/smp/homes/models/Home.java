package de.emn4tor.modules.smp.homes.models;

import de.emn4tor.YellowMCCoreV2;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.UUID;

public class Home {
    private final UUID playerUuid;
    private final String serverID;
    private final int homeNumber;
    private final String worldName;
    private final double x, y, z;
    private final float yaw, pitch;

    public Home(UUID playerUuid, String serverID, int homeNumber, Location loc) {
        this.playerUuid = playerUuid;
        this.serverID = serverID;
        this.homeNumber = homeNumber;

        this.worldName = (loc.getWorld() != null) ? loc.getWorld().getName() : "unknown_world";

        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    public boolean isLocal() {
        return this.serverID.equalsIgnoreCase(YellowMCCoreV2.getServerName());
    }

    public Location toBukkitLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        return new Location(world, x, y, z, yaw, pitch);
    }

    // Getters
    public UUID getPlayerUuid() { return playerUuid; }
    public String getServerID() { return serverID; }
    public int getHomeNumber() { return homeNumber; }
    public String getWorldName() { return worldName; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
}