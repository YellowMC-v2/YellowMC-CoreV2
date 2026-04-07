package de.emn4tor.modules.global.tpa.api;

import de.emn4tor.YellowMCCoreV2;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.function.Consumer;

public final class RandomTeleportAPI {
    private final YellowMCCoreV2 core;
    private final TeleportAPI teleportAPI;

    private final int MIN_X = -10000;
    private final int MAX_X = 10000;
    private final int MIN_Z = -10000;
    private final int MAX_Z = 10000;

    public RandomTeleportAPI(YellowMCCoreV2 core, TeleportAPI teleportAPI) {
        this.core = core;
        this.teleportAPI = teleportAPI;
    }

    public void rtpPlayer(Player player, String worldName) {
        var world = Bukkit.getWorld(worldName);

        if (world == null) return;

        this.safeRandomLocation(world, location -> {
            if (location != null) {
                this.teleportAPI.teleport(player, location);
            }
        });
    }

    private void safeRandomLocation(World world, Consumer<Location> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(this.core, () -> {
            var random = new Random();

            for (var i = 0; i < 20; i++) {
                var x = random.nextInt(MAX_X - MIN_X) + MIN_X;
                var z = random.nextInt(MAX_Z - MIN_Z) + MIN_Z;

                var chunkX = x >> 4;
                var chunkZ = z >> 4;

                var future = world.getChunkAtAsync(chunkX, chunkZ);
                future.join();

                int y;

                if (world.getEnvironment() == World.Environment.NETHER) {
                    for (y = 126; y > 30; y--) {
                        var below = world.getBlockAt(x, y - 1, z);
                        var feet = world.getBlockAt(x, y, z);
                        var head = world.getBlockAt(x, y + 1, z);

                        if (below.getType().isSolid()
                                && !below.isLiquid()
                                && feet.getType() == Material.AIR
                                && head.getType() == Material.AIR) {
                            Location safeLoc = new Location(world, x + 0.5, y, z + 0.5);
                            Bukkit.getScheduler().runTask(core, () -> callback.accept(safeLoc));
                            return;
                        }
                    }
                    continue;
                } else {
                    y = world.getHighestBlockYAt(x, z) + 1;
                }

                var below = world.getBlockAt(x, y - 1, z);
                var feet = world.getBlockAt(x, y, z);
                var head = world.getBlockAt(x, y + 1, z);

                if (below.getType().isSolid()
                        && !below.isLiquid()
                        && below.getType() != Material.BEDROCK
                        && feet.getType() == Material.AIR
                        && head.getType() == Material.AIR) {

                    Location safeLoc = new Location(world, x + 0.5, y, z + 0.5);
                    Bukkit.getScheduler().runTask(core, () -> callback.accept(safeLoc));
                    return;
                }
            }

            Bukkit.getScheduler().runTask(core, () -> callback.accept(null));
        });
    }
}
