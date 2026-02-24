package de.emn4tor.modules.global.muzzle.mute;

/*
 *  @author: Emn4tor
 *  @created: 04.05.2025
 */

import java.sql.*;
import java.util.UUID;
import de.emn4tor.data.SQLManager;

public class MuteManager {

    public MuteManager() {
        createTable();
    }

    private void createTable() {
        try (Connection conn = SQLManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS mutes (
                    uuid VARCHAR(36) PRIMARY KEY,
                    reason TEXT NOT NULL,
                    muted_by VARCHAR(36),
                    expires_at BIGINT
                );
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mute(UUID uuid, String reason, String mutedBy, long expiresAt) {
        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                 INSERT INTO mutes (uuid, reason, muted_by, expires_at)
                 VALUES (?, ?, ?, ?)
                 ON DUPLICATE KEY UPDATE reason=?, muted_by=?, expires_at=?;
             """)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, reason);
            stmt.setString(3, mutedBy);
            stmt.setLong(4, expiresAt);
            stmt.setString(5, reason);
            stmt.setString(6, mutedBy);
            stmt.setLong(7, expiresAt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unmute(UUID uuid) {
        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM mutes WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isMuted(UUID uuid) {
        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT expires_at FROM mutes WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long expiresAt = rs.getLong("expires_at");
                    if (expiresAt == 0 || expiresAt > System.currentTimeMillis()) {
                        return true;
                    } else {
                        unmute(uuid);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getReason(UUID uuid) {
        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT reason FROM mutes WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("reason");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "No reason specified.";
    }

    public long getExpiresAt(UUID uuid) {
        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT expires_at FROM mutes WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong("expires_at");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getRemainingTime(UUID uuid) {
        long expiresAt = getExpiresAt(uuid);
        if (expiresAt == 0) return "Permanent";

        long remaining = expiresAt - System.currentTimeMillis();
        if (remaining <= 0) return "Expired";

        long totalSeconds = remaining / 1000;
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0 || days > 0) sb.append(hours).append("h ");
        if (minutes > 0 || hours > 0 || days > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");

        return sb.toString().trim();
    }
}
