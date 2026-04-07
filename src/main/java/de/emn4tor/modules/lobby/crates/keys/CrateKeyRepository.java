package de.emn4tor.modules.lobby.crates.keys;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.SQLManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Persists virtual crate keys.
 *
 * Schema:
 *   crate_keys (
 *     player_uuid  VARCHAR(36)  — player's UUID
 *     crate_name   VARCHAR(64)  — crate id / name (e.g. "legendary")
 *     amount       INT          — key balance, never goes negative
 *     PRIMARY KEY (player_uuid, crate_name)
 *   )
 *
 * Keys are purely virtual — no item is ever given to the player.
 */
public class CrateKeyRepository {

    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS crate_keys (
                player_uuid  VARCHAR(36)  NOT NULL,
                crate_name   VARCHAR(64)  NOT NULL,
                amount       INT          NOT NULL DEFAULT 0,
                PRIMARY KEY (player_uuid, crate_name)
            );
            """;

    /** Add keys; creates the row if it does not exist yet. */
    private static final String GIVE_KEYS = """
            INSERT INTO crate_keys (player_uuid, crate_name, amount)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE amount = amount + VALUES(amount);
            """;

    /** Read balance. */
    private static final String GET_KEYS = """
            SELECT amount FROM crate_keys WHERE player_uuid = ? AND crate_name = ?;
            """;

    /**
     * Atomically deduct one key.
     * The WHERE clause guarantees the balance never goes below 0.
     */
    private static final String CONSUME_KEY = """
            UPDATE crate_keys
               SET amount = amount - 1
             WHERE player_uuid = ? AND crate_name = ? AND amount > 0;
            """;

    public CrateKeyRepository() {
        createTable();
    }

    /** How many virtual keys does this player have for the given crate? */
    public int getKeys(UUID playerUuid, String crateName) {
        try (Connection con = SQLManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(GET_KEYS)) {

            ps.setString(1, playerUuid.toString());
            ps.setString(2, crateName.toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("amount");
            }
        } catch (SQLException e) {
            log("getKeys failed for " + playerUuid + " / " + crateName, e);
        }
        return 0;
    }

    /**
     * Give {@code amount} virtual keys to a player for the named crate.
     *
     * @param playerUuid target player
     * @param crateName  crate id (stored lowercase)
     * @param amount     number of keys to add (must be > 0)
     */
    public void giveKeys(UUID playerUuid, String crateName, int amount) {
        if (amount <= 0) return;
        try (Connection con = SQLManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(GIVE_KEYS)) {

            ps.setString(1, playerUuid.toString());
            ps.setString(2, crateName.toLowerCase());
            ps.setInt(3, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            log("giveKeys failed for " + playerUuid + " / " + crateName, e);
        }
    }

    /**
     *  consume exactly one virtual key.
     *
     * @return {@code true} if a key was deducted; {@code false} if the player
     *         had no keys (crate open should be denied).
     */
    public boolean consumeKey(UUID playerUuid, String crateName) {
        try (Connection con = SQLManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(CONSUME_KEY)) {

            ps.setString(1, playerUuid.toString());
            ps.setString(2, crateName.toLowerCase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log("consumeKey failed for " + playerUuid + " / " + crateName, e);
        }
        return false;
    }

    private void createTable() {
        try (Connection con = SQLManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(CREATE_TABLE)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            log("Failed to create crate_keys table", e);
        }
    }

    private void log(String msg, Exception e) {
        YellowMCCoreV2.getInstance().getLogger()
                .severe("[CrateKeys] " + msg + ": " + e.getMessage());
    }
}