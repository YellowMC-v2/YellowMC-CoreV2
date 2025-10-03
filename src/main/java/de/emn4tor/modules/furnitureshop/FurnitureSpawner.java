package de.emn4tor.modules.furnitureshop;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FurnitureSpawner {

    // Room configuration
    private static class RoomConfig {
        final int schematicCount;
        final Location pasteLocation;
        final BlockVector3 clearPos1;
        final BlockVector3 clearPos2;

        RoomConfig(int count, Location paste, BlockVector3 p1, BlockVector3 p2) {
            this.schematicCount = count;
            this.pasteLocation = paste;
            this.clearPos1 = p1;
            this.clearPos2 = p2;
        }
    }

    private final Map<Integer, RoomConfig> rooms = new HashMap<>();
    private final Plugin plugin;
    private final String worldName;
    private final String schematicPath;
    private final Random random = new Random();
    private final Logger logger;

    public FurnitureSpawner(Plugin plugin) {
        this(plugin, "world", "plugins/FastAsyncWorldEdit/schematics/");
    }

    public FurnitureSpawner(Plugin plugin, String worldName, String schematicPath) {
        this.plugin = plugin;
        this.worldName = worldName;
        this.schematicPath = schematicPath;
        this.logger = plugin.getLogger();
        initializeRooms();
    }

    private void initializeRooms() {
        // Room 1 configuration
        rooms.put(1, new RoomConfig(
                1, // schematic count
                new Location(Bukkit.getWorld(worldName), -93, 74, 118),
                BlockVector3.at(-89, 78, 126),
                BlockVector3.at(-98, 73, 119)
        ));

        // Room 2 configuration
        rooms.put(2, new RoomConfig(
                2, // schematic count
                new Location(Bukkit.getWorld(worldName), -93, 74, 118),
                BlockVector3.at(-98, 78, 132),
                BlockVector3.at(-88, 72, 127)
        ));

        // Room 3 configuration
        rooms.put(3, new RoomConfig(
                1, // schematic count
                new Location(Bukkit.getWorld(worldName), -93, 74, 118),
                BlockVector3.at(-84, 78, 122),
                BlockVector3.at(-87, 72, 126)
        ));
    }

    /**
     * Adds or updates a room configuration
     */
    public void configureRoom(int roomId, int schematicCount, Location pasteLocation,
                              BlockVector3 clearPos1, BlockVector3 clearPos2) {
        rooms.put(roomId, new RoomConfig(schematicCount, pasteLocation, clearPos1, clearPos2));
    }

    /**
     * Starts generation task for a specific room
     */
    public void startGenerationTask(int roomId) {
        RoomConfig config = rooms.get(roomId);
        if (config == null) {
            logger.warning("Room " + roomId + " is not configured!");
            return;
        }

        if (config.schematicCount <= 0) {
            logger.warning("Room " + roomId + " has no schematics configured!");
            return;
        }

        // Clear the room first
        clearRoom(config);

        // Wait 10 seconds, then place new furniture
        new BukkitRunnable() {
            @Override
            public void run() {
                int schematicId = 1 + random.nextInt(config.schematicCount);
                String schematicName = "furniture_" + roomId + "_" + schematicId;
                placeFurnitureSchematic(config.pasteLocation, schematicName);
            }
        }.runTaskLater(plugin, 200L); // 200 ticks = 10 seconds
    }

    /**
     * Starts generation task for all configured rooms
     */
    public void startGenerationTaskForAllRooms() {
        for (int roomId : rooms.keySet()) {
            RoomConfig config = rooms.get(roomId);
            if (config.schematicCount > 0) {
                startGenerationTask(roomId);
            }
        }
    }

    /**
     * Clears a room's content and entities
     */
    private void clearRoom(RoomConfig config) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    org.bukkit.World bukkitWorld = Bukkit.getWorld(worldName);
                    if (bukkitWorld == null) {
                        logger.severe("World '" + worldName + "' not found!");
                        return;
                    }

                    World weWorld = BukkitAdapter.adapt(bukkitWorld);
                    CuboidRegion region = new CuboidRegion(weWorld, config.clearPos1, config.clearPos2);

                    // Clear blocks asynchronously with WorldEdit
                    try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                        editSession.setBlocks(region, BukkitAdapter.adapt(Material.AIR.createBlockData()));
                    }

                    // Remove entities (must be done sync)
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            removeEntitiesInRegion(bukkitWorld, region);
                        }
                    }.runTask(plugin);

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error clearing room", e);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Removes all entities within a region
     */
    private void removeEntitiesInRegion(org.bukkit.World world, CuboidRegion region) {
        int removed = 0;
        for (org.bukkit.entity.Entity entity : world.getEntities()) {
            BlockVector3 entityPos = BlockVector3.at(
                    entity.getLocation().getBlockX(),
                    entity.getLocation().getBlockY(),
                    entity.getLocation().getBlockZ()
            );

            if (region.contains(entityPos)) {
                entity.remove();
                removed++;
            }
        }
        logger.info("Removed " + removed + " entities from region");
    }

    /**
     * Places a schematic at the specified location
     */
    private void placeFurnitureSchematic(Location location, String schematicName) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    World weWorld = BukkitAdapter.adapt(location.getWorld());

                    File schematicFile = new File(schematicPath + schematicName + ".schem");
                    if (!schematicFile.exists()) {
                        logger.warning("Schematic file not found: " + schematicFile.getPath());
                        return;
                    }

                    ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
                    if (format == null) {
                        logger.severe("Unknown schematic format for: " + schematicName);
                        return;
                    }

                    Clipboard clipboard;
                    try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
                        clipboard = reader.read();
                    }

                    try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                        Operation operation = new ClipboardHolder(clipboard)
                                .createPaste(editSession)
                                .to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
                                .ignoreAirBlocks(false)
                                .build();
                        Operations.complete(operation);
                    }

                    logger.info("Successfully placed schematic: " + schematicName);

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error placing schematic: " + schematicName, e);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Legacy method for backwards compatibility
     */
    @Deprecated
    public void deleteSortiment() {
        RoomConfig config = rooms.get(2); // Default to room 2
        if (config != null) {
            clearRoom(config);
        }
    }
}