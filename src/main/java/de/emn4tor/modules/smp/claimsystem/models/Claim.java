package de.emn4tor.modules.smp.claimsystem.models;

import java.util.List;
import java.util.UUID;

/**
 * Represents a claimed chunk in a specific world.
 * <p>
 * Each Claim contains the owner's UUID, a list of trusted players who can
 * interact with the chunk, and the chunk coordinates (X, Z) along with
 * the world name. This class is immutable once constructed.
 */
public class Claim {

    private final UUID ownerUUID;
    private final List<UUID> trustedUUIDs;
    private final long chunkX;
    private final long chunkZ;
    private final String worldName;

    /**
     * Constructs a new Claim.
     *
     * @param ownerUUID the UUID of the player who owns the claim
     * @param trustedUUIDs a list of UUIDs representing players trusted in this claim
     * @param chunkX the X coordinate of the chunk
     * @param chunkZ the Z coordinate of the chunk
     * @param worldName the name of the world the chunk is in
     */
    public Claim(UUID ownerUUID, List<UUID> trustedUUIDs, long chunkX, long chunkZ, String worldName) {
        this.ownerUUID = ownerUUID;
        this.trustedUUIDs = trustedUUIDs;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.worldName = worldName;
    }

    /**
     * Returns the UUID of the owner of this claim.
     *
     * @return the owner's UUID
     */
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    /**
     * Returns the list of UUIDs of trusted players who can interact with this claim.
     *
     * @return a list of trusted player UUIDs
     */
    public List<UUID> getTrustedUUIDs() {
        return trustedUUIDs;
    }

    /**
     * Returns the X coordinate of the claimed chunk.
     *
     * @return the chunk's X coordinate
     */
    public long getChunkX() {
        return chunkX;
    }

    /**
     * Returns the Z coordinate of the claimed chunk.
     *
     * @return the chunk's Z coordinate
     */
    public long getChunkZ() {
        return chunkZ;
    }

    /**
     * Returns the name of the world where the chunk is located.
     *
     * @return the world name
     */
    public String getWorldName() {
        return worldName;
    }
}
