package de.emn4tor.modules.smp.homes.repository;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.smp.homes.models.Home;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class HomeRepository {
    private final Connection connection;

    public HomeRepository(Connection connection) {
        this.connection = connection;
    }

    public Optional<Home> findHomesByUUID(@NotNull UUID uuid) {
        try (var preparedStatement = connection.prepareStatement("SELECT * FROM player_homes WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());

            var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                var home = Home.builder()
                        .uuid(UUID.fromString(resultSet.getString("uuid")))
                        .serverID(resultSet.getString("server_id"))
                        .homeNumber(resultSet.getInt("home_number"))
                        .worldName(resultSet.getString("world_name"))
                        .x(resultSet.getDouble("x"))
                        .y(resultSet.getDouble("y"))
                        .z(resultSet.getDouble("z"))
                        .yaw(resultSet.getFloat("yaw"))
                        .pitch(resultSet.getFloat("pitch"))
                        .build();

                return Optional.of(home);
            }
        } catch (SQLException exception) {
            YellowMCCoreV2.getInstance().getLogger().severe(exception.getMessage());
        }

        return Optional.empty();
    }

    public void createHome(@NonNull Home home) {
        try (var preparedStatement = connection.prepareStatement("INSERT INTO player_homes (uuid, server_id, home_number, world_name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, home.uuid().toString());
            preparedStatement.setString(2, home.serverID());
            preparedStatement.setInt(3, home.homeNumber());
            preparedStatement.setString(4, home.worldName());
            preparedStatement.setDouble(5, home.x());
            preparedStatement.setDouble(6, home.y());
            preparedStatement.setDouble(7, home.z());
            preparedStatement.setFloat(8, home.yaw());
            preparedStatement.setFloat(9, home.pitch());

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            YellowMCCoreV2.getInstance().getLogger().severe(exception.getMessage());
        }

    }
}