package de.emn4tor.modules.global.muzzle.bans;

/*
 *  @author: Emn4tor
 *  @created: 07.05.2025
 */

import de.emn4tor.data.SQLManager;
import de.emn4tor.data.RedisManager;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BanManager {
    private final RedisManager redis = RedisManager.getInstance();

    public BanManager() {
        createTable();
    }

    private void createTable() {
        try (Connection conn = SQLManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS bans (
                    uuid VARCHAR(36) PRIMARY KEY,
                    reason TEXT NOT NULL,
                    banned_by VARCHAR(36),
                    ban_time BIGINT,
                    expires_at BIGINT,
                    active BOOLEAN DEFAULT TRUE
                );
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void kickPlayer(String playerName, String reason, String admin) {
        UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
        String kickMessage = formatKickMessage(reason, admin);

        JSONObject data = new JSONObject();
        data.put("reason", kickMessage);
        data.put("uuid", uuid);

        JSONObject message = new JSONObject();
        message.put("messages", data.toString());

        redis.publish("redivelocity-kick", message.toString());
    }

    public void kickPlayerBan(String uuid, String reason, String admin, String duration) {
        String banMessage = formatBanMessage(reason, admin, duration);

        JSONObject data = new JSONObject();
        data.put("reason", banMessage);
        data.put("uuid", uuid);

        JSONObject message = new JSONObject();
        message.put("messages", data.toString());

        redis.publish("redivelocity-kick", message.toString());
    }

    private String formatKickMessage(String reason, String admin) {
        return "<gradient:#FFEF0F:#FF8600>YellowMC</gradient><br><br><gray>You got kicked!<br><br><color:#ffc800>Reason:</color> <white>"
                + reason + "<br><br><color:#ffc800>Admin:</color> " + admin
                + "<br><br><click:open_url:'https://yellowmc.de/discord'><gray>https://yellowmc.de/discord</click>";
    }

    private String formatBanMessage(String reason, String admin, String duration) {
        return "<gradient:#FFEF0F:#FF8600>YellowMC</gradient><br><br><gray>You are banned for<color:#ffed9e>"
                + duration + "</color><br><br><color:#ffc800>Reason:</color> <white>"
                + (reason != null ? reason : "No Reason specified.")
                + "<br><br><color:#ffc800>Admin:</color> "
                + (admin != null ? admin : "System")
                + "<br><br><click:open_url:'https://yellowmc.de/discord'><gray>https://yellowmc.de/discord</click>";
    }

    public void banPlayer(String admin, String targetUUID, String reason, String duration) {
        kickPlayerBan(targetUUID, reason, admin, duration);

        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                 INSERT INTO bans (uuid, reason, banned_by, ban_time, expires_at, active)
                 VALUES (?, ?, ?, ?, ?, ?)
                 ON DUPLICATE KEY UPDATE reason=?, banned_by=?, ban_time=?, expires_at=?, active=?;
             """)) {
            long now = System.currentTimeMillis();
            long expiry = now + parseDuration(duration);

            stmt.setString(1, targetUUID);
            stmt.setString(2, reason);
            stmt.setString(3, admin);
            stmt.setLong(4, now);
            stmt.setLong(5, expiry);
            stmt.setBoolean(6, true);

            stmt.setString(7, reason);
            stmt.setString(8, admin);
            stmt.setLong(9, now);
            stmt.setLong(10, expiry);
            stmt.setBoolean(11, true);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private long parseDuration(String duration) {
        String[] parts = duration.split(" ");
        long totalMillis = 0;
        for (String part : parts) {
            if (part.endsWith("s")) totalMillis += Integer.parseInt(part.replace("s", "")) * 1000L;
            else if (part.endsWith("m")) totalMillis += Integer.parseInt(part.replace("m", "")) * 60_000L;
            else if (part.endsWith("h")) totalMillis += Integer.parseInt(part.replace("h", "")) * 3_600_000L;
            else if (part.endsWith("d")) totalMillis += Integer.parseInt(part.replace("d", "")) * 86_400_000L;
        }
        return totalMillis;
    }

    private String formatDuration(long millis) {
        long days = millis / 86_400_000L;
        millis %= 86_400_000L;
        long hours = millis / 3_600_000L;
        millis %= 3_600_000L;
        long minutes = millis / 60_000L;
        millis %= 60_000L;
        long seconds = millis / 1000L;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    public CompletableFuture<Boolean> isBannedAsync(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = SQLManager.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bans WHERE uuid = ?")) {
                stmt.setString(1, uuid);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) return false;
                    if (!rs.getBoolean("active")) return false;

                    long expiresAt = rs.getLong("expires_at");
                    if (expiresAt > System.currentTimeMillis()) {
                        kickPlayerBan(uuid, rs.getString("reason"), rs.getString("banned_by"),
                                formatDuration(expiresAt - System.currentTimeMillis()));
                        return true;
                    }
                    if (expiresAt == 0) {
                        kickPlayerBan(uuid, rs.getString("reason"), rs.getString("banned_by"), "Permanent");
                        return true;
                    }

                    unbanPlayer(uuid);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    private void unbanPlayer(String uuid) {
        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE bans SET active = FALSE WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getBanReason(String uuid) {
        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT reason FROM bans WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("reason");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "no reason specified.";
    }
}
