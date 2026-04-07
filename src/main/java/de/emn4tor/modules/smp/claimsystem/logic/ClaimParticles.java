package de.emn4tor.modules.smp.claimsystem.logic;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.smp.claimsystem.models.ChunkKey;
import de.emn4tor.modules.smp.claimsystem.models.Claim;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Particle.DustOptions;

import java.util.*;

/**
 * Handles animated construction-style particle borders around player claims.
 * <p>
 * Displays alternating orange and black particles with a moving wave effect
 * along all connected claimed chunks. Particles gradually rise for visibility
 * above terrain or partially underground blocks.
 * </p>
 */
public class ClaimParticles {

    private final ClaimManager claimManager;
    private final YellowMCCoreV2 plugin;

    public ClaimParticles(ClaimManager claimManager, YellowMCCoreV2 plugin) {
        this.claimManager = claimManager;
        this.plugin = plugin;
    }

    /**
     * Shows a construction-style animated particle border around all connected claims.
     *
     * @param player    the owner of the claims
     * @param startingX the starting chunk X coordinate
     * @param startingZ the starting chunk Z coordinate
     * @param worldName the world name
     */
    public void showClaimBorder(Player player, long startingX, long startingZ, String worldName) {
        Set<ChunkKey> visited = new HashSet<>();
        Queue<ChunkKey> queue = new LinkedList<>();
        queue.add(new ChunkKey(worldName, startingX, startingZ));

        Set<Location> borderBlocks = new LinkedHashSet<>(); // Preserve insertion order for animation

        // BFS to find all connected claims
        while (!queue.isEmpty()) {
            ChunkKey current = queue.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            Claim claim = claimManager.getClaim(current.worldName(), current.x(), current.z());
            if (claim != null && claim.getOwnerUUID().equals(player.getUniqueId())) {
                int cx = (int) current.x() << 4;
                int cz = (int) current.z() << 4;

                for (int x = cx; x < cx + 16; x++) {
                    for (int z = cz; z < cz + 16; z++) {
                        if (x == cx || x == cx + 15 || z == cz || z == cz + 15) {
                            int y = player.getWorld().getHighestBlockYAt(x, z) + 1;
                            borderBlocks.add(new Location(player.getWorld(), x + 0.5, y, z + 0.5));
                        }
                    }
                }

                long[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
                for (long[] dir : directions) {
                    ChunkKey neighbor = new ChunkKey(current.worldName(), current.x() + dir[0], current.z() + dir[1]);
                    if (!visited.contains(neighbor)) queue.add(neighbor);
                }
            }
        }

        // Define construction colors
        DustOptions[] colors = new DustOptions[]{
                new DustOptions(Color.fromRGB(255, 165, 0), 1.2f), // Orange
                new DustOptions(Color.BLACK, 1.2f)                 // Black
        };

        // Animate particles with rising and moving wave effect
        new BukkitRunnable() {
            int ticks = 0;
            final double riseSpeed = 0.05;

            @Override
            public void run() {
                if (ticks++ > 60) {
                    cancel();
                    return;
                }

                int i = 0;
                for (Location loc : borderBlocks) {
                    double yOffset = ticks * riseSpeed;
                    Location animatedLoc = loc.clone().add(0, yOffset, 0);

                    // Wave effect along border
                    int colorIndex = (i + ticks / 2) % colors.length;
                    player.spawnParticle(Particle.DUST, animatedLoc, 1, colors[colorIndex]);
                    i++;
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
