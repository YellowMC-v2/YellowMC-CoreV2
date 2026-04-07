package de.emn4tor.modules.smp.claimsystem.models;

/**
 * Immutable key representing a unique chunk in a specific world.
 * <p>
 * This record is used as a key in maps (e.g., {@link java.util.Map})
 * to identify claimed chunks. It combines the world name and chunk
 * coordinates (X and Z) to uniquely identify a location.
 */
public record ChunkKey(String worldName, long x, long z) {

    /**
     * Constructs a new ChunkKey.
     *
     * @param worldName the name of the world the chunk is in
     * @param x the X coordinate of the chunk
     * @param z the Z coordinate of the chunk
     */
    public ChunkKey {
        // implicit constructor body; record fields are final and non-null
    }
}
