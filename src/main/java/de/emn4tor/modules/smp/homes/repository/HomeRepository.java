package de.emn4tor.modules.smp.homes.repository;

import de.emn4tor.modules.smp.homes.models.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;
import java.util.UUID;

public class HomeRepository {
    private final Connection connection;

    public HomeRepository(Connection connection) {
        this.connection = connection;
    }

    public void saveHome(Home home) throws SQLException {
        String sql = "INSERT INTO homes (player_uuid, server_id, home_number, world_name, x, y, z, yaw, pitch) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE server_id=VALUES(server_id), world_name=VALUES(world_name), x=VALUES(x), " +
                "y=VALUES(y), z=VALUES(z), yaw=VALUES(yaw), pitch=VALUES(pitch);";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, home.getPlayerUuid().toString());
            pstmt.setString(2, home.getServerID()); // New field
            pstmt.setInt(3, home.getHomeNumber());
            pstmt.setString(4, home.getWorldName());
            pstmt.setDouble(5, home.getX());
            pstmt.setDouble(6, home.getY());
            pstmt.setDouble(7, home.getZ());
            pstmt.setFloat(8, home.getYaw());
            pstmt.setFloat(9, home.getPitch());
            pstmt.executeUpdate();
        }
    }

    public Home getHome(UUID uuid, int number) throws SQLException {
        String sql = "SELECT * FROM homes WHERE player_uuid = ? AND home_number = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setInt(2, number);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Home(
                        uuid,
                        rs.getString("server_id"),
                        number,
                        new Location(
                                Bukkit.getWorld(rs.getString("world_name")),
                                rs.getDouble("x"),
                                rs.getDouble("y"),
                                rs.getDouble("z"),
                                rs.getFloat("yaw"),
                                rs.getFloat("pitch")
                        )
                );
            }
        }
        return null;
    }
}