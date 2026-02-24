package de.emn4tor.modules.smp.claimsystem.logic;


import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.SQLManager;
import de.emn4tor.modules.smp.claimsystem.models.ChunkKey;
import de.emn4tor.modules.smp.claimsystem.models.Claim;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;


public class ClaimManager {

    private final Map<ChunkKey, Claim> claimsMap = new HashMap<>();
    private final YellowMCCoreV2 plugin = YellowMCCoreV2.getInstance();
    /**
     * Initializes the ClaimManager by loading all claims from the database into memory.
     */
    public ClaimManager(YellowMCCoreV2 plugin) {
        loadAllClaims();
        setupUserDataTable();
        setupTrustedTable();
    }

    /**
     * Adds a new claim to the specified chunk and persists it to the database.
     *
     * @param claim the Claim object to add
     * @return true if the claim was added successfully; false if the chunk is already claimed
     */
    public boolean addClaim(Claim claim) {
        ChunkKey key = new ChunkKey(claim.getWorldName(), claim.getChunkX(), claim.getChunkZ());

        if (claimsMap.containsKey(key)) {
            return false;
        }

        claimsMap.put(key, claim);
        persistClaim(claim);
        return true;
    }

    /**
     * Removes an existing claim from the specified Claim and deletes it from the database.
     *
     * @param claim the Claim object to remove
     */
    public void removeClaim(Claim claim) {
        ChunkKey key = new ChunkKey(claim.getWorldName(), claim.getChunkX(), claim.getChunkZ());
        claimsMap.remove(key);
        deleteClaimFromDB(claim);
    }

    /** Removes existing claim from the specified chunk coordinates and deletes it from the database.
     *
     * @param worldName the name of the world
     * @param chunkX the X coordinate of the chunk
     * @param chunkZ the Z coordinate of the chunk
     */
    public void removeClaim(String worldName, long chunkX, long chunkZ) {
        ChunkKey key = new ChunkKey(worldName, chunkX, chunkZ);
        Claim claim = claimsMap.remove(key);
        if (claim != null) {
            deleteClaimFromDB(claim);
        }
    }

    /**
     * Retrieves the claim associated with the specified chunk.
     *
     * @param worldName the name of the world
     * @param chunkX the X coordinate of the chunk
     * @param chunkZ the Z coordinate of the chunk
     * @return the Claim if the chunk is claimed, or null if unclaimed
     */
    public Claim getClaim(String worldName, long chunkX, long chunkZ) {
        return claimsMap.get(new ChunkKey(worldName, chunkX, chunkZ));
    }

    /**
     * Determines if a player can modify blocks within the specified chunk.
     *
     * @param playerUUID the UUID of the player
     * @param worldName the name of the world
     * @param chunkX the X coordinate of the chunk
     * @param chunkZ the Z coordinate of the chunk
     * @return true if the player is the owner, is trusted, or the chunk is unclaimed; false otherwise
     */
    public boolean canModify(UUID playerUUID, String worldName, long chunkX, long chunkZ) {
        Claim claim = getClaim(worldName, chunkX, chunkZ);
        if (claim == null) return true;
        return claim.getOwnerUUID().equals(playerUUID) || claim.getTrustedUUIDs().contains(playerUUID);
    }

    /**
     * Checks whether a chunk is already claimed (in-memory only).
     *
     * @param worldName the name of the world
     * @param chunkX the X coordinate of the chunk
     * @param chunkZ the Z coordinate of the chunk
     * @return true if the chunk is claimed in the cache; false otherwise
     */
    public boolean isClaimed(String worldName, long chunkX, long chunkZ) {
        return claimsMap.containsKey(new ChunkKey(worldName, chunkX, chunkZ));
    }

    /** Returns the number of claims of the specified player
     *
     * @param playerUUID the UUID of the player
     * @return the number of claims the player has
     */
    public int getPlayerClaimCount(UUID playerUUID) {
        int count = 0;
        for (Claim claim : claimsMap.values()) {
            if (claim.getOwnerUUID().equals(playerUUID)) {
                count++;
            }
        }
        return count;
    }

    /** Returns the maximum number of claims allowed for the specified player
     *
     * @param playerUUID the UUID of the player
     * @return the maximum number of claims the player is allowed to have
     */
    public int getPlayerMaxClaims(UUID playerUUID) {
        String sql = "INSERT INTO userdata (uuid, max_claims) " +
                "VALUES (?, 32) " +
                "ON DUPLICATE KEY UPDATE uuid=uuid";
        String selectSql = "SELECT max_claims FROM userdata WHERE uuid = ?";
        try (Connection connection = SQLManager.getInstance().getConnection()) {
            try (PreparedStatement insertStmt = connection.prepareStatement(sql)) {
                insertStmt.setString(1, playerUUID.toString());
                insertStmt.executeUpdate();
            }
            try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
                selectStmt.setString(1, playerUUID.toString());
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("max_claims");
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get player max claims: " + e.getMessage());
        }
        return 32;
    }


    /** Updates the maximum number of claims allowed for the specified player
     *
     * @param playerUUID the UUID of the player
     * @param newMaxClaims the new maximum number of claims to set
     */

    public void updatePlayerMaxClaims(UUID playerUUID, int newMaxClaims) {
        String sql = "UPDATE userdata SET max_claims = ? WHERE uuid = ?";
        try (Connection connection = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newMaxClaims);
            stmt.setString(2, playerUUID.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update player max claims: " + e.getMessage());
        }
    }

    /** Returns if the specified player is allowed to get another claim
     *
     * @param playerUUID the UUID of the player
     * @return true if the player can get another claim; false otherwise
     */
    public boolean canPlayerClaim(UUID playerUUID) {
        return getPlayerClaimCount(playerUUID) < getPlayerMaxClaims(playerUUID);
    }

    /** Returns the amount of claims from the specified player
     *
     * @param playerUUID the UUID of the player
     * @return the amount of claims the player has
     */
    public int getTotalClaimsByPlayer(UUID playerUUID) {
        return (int) claimsMap.values().stream()
                .filter(claim -> claim.getOwnerUUID().equals(playerUUID))
                .count();
    }

    /** Return total number of claims in the system
     *
     * @return the total number of claims
     */
    public int getTotalClaims() {
        return claimsMap.size();
    }

    /** Reloads all claims from the database into memory, replacing the current in-memory cache. */
    public void reloadClaims() {
        claimsMap.clear();
        loadAllClaims();
    }

    /** Makes sure trusted table exists */
    private void setupTrustedTable() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS claim_trusted (" +
                "trustedUUID VARCHAR(36) NOT NULL, " +
                "chunkX BIGINT NOT NULL, " +
                "chunkZ BIGINT NOT NULL, " +
                "worldName VARCHAR(255) NOT NULL, " +
                "PRIMARY KEY (trustedUUID, chunkX, chunkZ, worldName)" +
                ")";
        try (Connection connection = SQLManager.getInstance().getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSql);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to setup claim trusted table " + e.getMessage());
        }
    }


    /** Loads all claims from the database into memory. */
    private void loadAllClaims() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS claims (" +
                "ownerUUID VARCHAR(36) NOT NULL, " +
                "chunkX BIGINT NOT NULL, " +
                "chunkZ BIGINT NOT NULL, " +
                "worldName VARCHAR(255) NOT NULL, " +
                "PRIMARY KEY (chunkX, chunkZ, worldName)" +
                ")";
        String selectSql = "SELECT ownerUUID, chunkX, chunkZ, worldName FROM claims";

        try (Connection connection = SQLManager.getInstance().getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSql);

            try (PreparedStatement ps = connection.prepareStatement(selectSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UUID ownerUUID = UUID.fromString(rs.getString("ownerUUID"));
                    long chunkX = rs.getLong("chunkX");
                    long chunkZ = rs.getLong("chunkZ");
                    String worldName = rs.getString("worldName");

                    List<UUID> trusted = loadTrusted(connection, chunkX, chunkZ, worldName);
                    Claim claim = new Claim(ownerUUID, trusted, chunkX, chunkZ, worldName);
                    claimsMap.put(new ChunkKey(worldName, chunkX, chunkZ), claim);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load claims " + e.getMessage());
        }
    }


    /** Makes sure userdata table exists */
    private void setupUserDataTable() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS userdata (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "max_claims INT DEFAULT 32" +
                ")";
        try (Connection connection = SQLManager.getInstance().getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSql);
        }
        catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE,"Failed to setup user data table" + e.getMessage());
        }
    }


    /** Persists a claim to the database */
    private void persistClaim(Claim claim) {
        String sql = "INSERT INTO claims (ownerUUID, chunkX, chunkZ, worldName) VALUES (?, ?, ?, ?)";
        try (Connection connection = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, claim.getOwnerUUID().toString());
            stmt.setLong(2, claim.getChunkX());
            stmt.setLong(3, claim.getChunkZ());
            stmt.setString(4, claim.getWorldName());
            stmt.executeUpdate();

            for (UUID trusted : claim.getTrustedUUIDs()) {
                addTrustedToDB(connection, claim, trusted);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to persist claim " + e.getMessage());
        }
    }


    /** Deletes a claim from the database */
    private void deleteClaimFromDB(Claim claim) {
        String sql = "DELETE FROM claims WHERE chunkX = ? AND chunkZ = ? AND worldName = ?";
        try (Connection connection = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, claim.getChunkX());
            stmt.setLong(2, claim.getChunkZ());
            stmt.setString(3, claim.getWorldName());
            stmt.executeUpdate();

            String trustedSql = "DELETE FROM claim_trusted WHERE chunkX = ? AND chunkZ = ? AND worldName = ?";
            try (PreparedStatement trustedStmt = connection.prepareStatement(trustedSql)) {
                trustedStmt.setLong(1, claim.getChunkX());
                trustedStmt.setLong(2, claim.getChunkZ());
                trustedStmt.setString(3, claim.getWorldName());
                trustedStmt.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to delete claim " + e.getMessage());
        }
    }


    private List<UUID> loadTrusted(Connection connection, long chunkX, long chunkZ, String worldName) {
        String sql = "SELECT trustedUUID FROM claim_trusted WHERE chunkX = ? AND chunkZ = ? AND worldName = ?";
        List<UUID> trusted = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, chunkX);
            stmt.setLong(2, chunkZ);
            stmt.setString(3, worldName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) trusted.add(UUID.fromString(rs.getString("trustedUUID")));
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load trusted list " + e.getMessage());
        }
        return trusted;
    }

    private void addTrustedToDB(Connection connection, Claim claim, UUID trustedUUID) throws SQLException {
        String sql = "INSERT IGNORE INTO claim_trusted (trustedUUID, chunkX, chunkZ, worldName) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, trustedUUID.toString());
            stmt.setLong(2, claim.getChunkX());
            stmt.setLong(3, claim.getChunkZ());
            stmt.setString(4, claim.getWorldName());
            stmt.executeUpdate();
        }
    }

    public void removeTrusted(Claim claim, UUID trustedUUID) {
        if (claim.getTrustedUUIDs().remove(trustedUUID)) {
            String sql = "DELETE FROM claim_trusted WHERE trustedUUID = ? AND chunkX = ? AND chunkZ = ? AND worldName = ?";
            try (Connection connection = SQLManager.getInstance().getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, trustedUUID.toString());
                stmt.setLong(2, claim.getChunkX());
                stmt.setLong(3, claim.getChunkZ());
                stmt.setString(4, claim.getWorldName());
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to remove trusted player " + e.getMessage());
            }
        }
    }


    public void addTrusted(Claim claim, UUID trustedUUID) {
        if (!claim.getTrustedUUIDs().contains(trustedUUID)) {
            claim.getTrustedUUIDs().add(trustedUUID);
            try (Connection connection = SQLManager.getInstance().getConnection()) {
                addTrustedToDB(connection, claim, trustedUUID);
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to add trusted player " + e.getMessage());
            }
        }
    }

    /**
     * Trusts a player on all connected chunks owned by the owner starting from a given chunk.
     *
     * @param ownerUUID the UUID of the chunk owner
     * @param targetUUID the UUID of the player to trust
     * @param startingX the starting chunk X coordinate
     * @param startingZ the starting chunk Z coordinate
     * @param worldName the world name
     */
    public void trustConnectedChunks(UUID ownerUUID, UUID targetUUID, long startingX, long startingZ, String worldName) {
        Set<ChunkKey> visited = new HashSet<>();
        Queue<ChunkKey> queue = new LinkedList<>();
        queue.add(new ChunkKey(worldName, startingX, startingZ));

        while (!queue.isEmpty()) {
            ChunkKey current = queue.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            Claim claim = getClaim(current.worldName(), current.x(), current.z());
            if (claim != null && claim.getOwnerUUID().equals(ownerUUID)) {
                addTrusted(claim, targetUUID);

                // Check adjacent chunks (4 directions)
                long[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};
                for (long[] dir : directions) {
                    long nx = current.x() + dir[0];
                    long nz = current.z() + dir[1];
                    ChunkKey neighbor = new ChunkKey(current.worldName(), nx, nz);

                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }
    }

    /**
     * Removes trust from a player on all connected chunks owned by the owner starting from a given chunk.
     *
     * @param ownerUUID the UUID of the chunk owner
     * @param targetUUID the UUID of the player to untrust
     * @param startingX the starting chunk X coordinate
     * @param startingZ the starting chunk Z coordinate
     * @param worldName the world name
     */
    public void untrustConnectedChunks(UUID ownerUUID, UUID targetUUID, long startingX, long startingZ, String worldName) {
        Set<ChunkKey> visited = new HashSet<>();
        Queue<ChunkKey> queue = new LinkedList<>();
        queue.add(new ChunkKey(worldName, startingX, startingZ));

        while (!queue.isEmpty()) {
            ChunkKey current = queue.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            Claim claim = getClaim(current.worldName(), current.x(), current.z());
            if (claim != null && claim.getOwnerUUID().equals(ownerUUID)) {
                removeTrusted(claim, targetUUID);

                // Check adjacent chunks (4 directions)
                long[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};
                for (long[] dir : directions) {
                    long nx = current.x() + dir[0];
                    long nz = current.z() + dir[1];
                    ChunkKey neighbor = new ChunkKey(current.worldName(), nx, nz);

                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }
    }










}