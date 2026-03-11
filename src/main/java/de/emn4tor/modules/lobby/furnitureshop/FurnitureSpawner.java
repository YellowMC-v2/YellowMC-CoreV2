package de.emn4tor.modules.lobby.furnitureshop;

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
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
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
        FileConfiguration config = YellowMCCoreV2.getInstance().getConfig();

        for (int i = 1; i <= 3; i++) {
            String basePath = "furniture.room_" + i + ".";

            int schematicCount = config.getInt(basePath + "roomCount", 1);
            Location roomLocation = new Location(
                    Bukkit.getWorld(config.getString(basePath + "world", worldName)),
                    config.getDouble(basePath + "spawn.x", 0),
                    config.getDouble(basePath + "spawn.y", 0),
                    config.getDouble(basePath + "spawn.z", 0)
            );
            BlockVector3 vector1 = BlockVector3.at(
                    config.getInt(basePath + "vector1.x", 0),
                    config.getInt(basePath + "vector1.y", 0),
                    config.getInt(basePath + "vector1.z", 0)
            );
            BlockVector3 vector2 = BlockVector3.at(
                    config.getInt(basePath + "vector2.x", 0),
                    config.getInt(basePath + "vector2.y", 0),
                    config.getInt(basePath + "vector2.z", 0)
            );

            rooms.put(i, new RoomConfig(schematicCount, roomLocation, vector1, vector2));
        }
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

        // Kick players from the region
        kickPlayersFromRegion();
        // Close doors
        closeDoors();

        // Clear the room first
        clearRoom(config);

        // Wait 30 seconds, then place new furniture
        new BukkitRunnable() {
            @Override
            public void run() {
                int schematicId = 1 + random.nextInt(config.schematicCount);
                String schematicName = "furniture_" + roomId + "_" + schematicId;
                placeFurnitureSchematic(config.pasteLocation, schematicName);
            }
        }.runTaskLater(plugin, 20 * 30L); // 20 ticks * 30 seconds
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
     *  Kicks all players from the area if any are present
     */
    private void kickPlayersFromRegion() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(FurnitureHoverListener.isInShop(player)) {
                player.teleport(new Location(Bukkit.getWorld(worldName), 75.41, 63.00, -118.71, 90, 0));
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "furniture-refresh-notification", FormatService.MessageType.SYSTEM));
            }
        }
    }

    /**
     * Closes the doors of the furniture shop
     */
    private void closeDoors() {
        org.bukkit.World bukkitWorld = Bukkit.getWorld(worldName);
        if (bukkitWorld == null) {
            logger.severe("World '" + worldName + "' not found!");
            return;
        }

        int x1 = 66, y1 = 65, z1 = -117;
        int x2 = 66, y2 = 68, z2 = -121;

        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        Bukkit.getScheduler().runTask(YellowMCCoreV2.getInstance(), () -> {
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        bukkitWorld.getBlockAt(x, y, z).setType(Material.SPRUCE_PLANKS);
                    }
                }
            }

            Bukkit.getScheduler().runTaskLater(YellowMCCoreV2.getInstance(), () -> {
                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            bukkitWorld.getBlockAt(x, y, z).setType(Material.AIR);
                        }
                    }
                }
            }, 600L); // 30 seconds = 600 ticks
        });
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