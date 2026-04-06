package de.emn4tor.modules.smp.homes.repository;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.data.SQLManager;
import de.emn4tor.modules.smp.homes.models.Home;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HomeRepository {
    private final SQLManager sqlManager;

    public HomeRepository(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public List<Home> findHomesByUUID(@NotNull UUID uuid) {
        List<Home> homes = new ArrayList<>();
        try (Connection connection = sqlManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("SELECT * FROM player_homes WHERE uuid = ?")) {
                preparedStatement.setString(1, uuid.toString());

                var resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
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

                    homes.add(home);
                }
            }
        } catch (SQLException exception) {
            YellowMCCoreV2.getInstance().getLogger().severe(exception.getMessage());
        }

        return homes;
    }

    public void createHome(@NonNull Home home) {
        try (Connection connection = sqlManager.getConnection()) {
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
            }
        } catch (SQLException exception) {
            YellowMCCoreV2.getInstance().getLogger().severe(exception.getMessage());
        }

    }

    public void deleteHome(@NotNull UUID uuid, int homeNumber) {
        try (Connection connection = sqlManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("DELETE FROM player_homes WHERE uuid = ? AND home_number = ?")) {
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setInt(2, homeNumber);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            YellowMCCoreV2.getInstance().getLogger().severe(exception.getMessage());
        }
    }

    public Optional<Home> findHomeByUUIDAndNumber(@NotNull UUID uuid, int homeNumber) {
        try (Connection connection = sqlManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("SELECT * FROM player_homes WHERE uuid = ? AND home_number = ?")) {
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setInt(2, homeNumber);

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
            }
        } catch (SQLException exception) {
            YellowMCCoreV2.getInstance().getLogger().severe(exception.getMessage());
        }

        return Optional.empty();
    }

    public void createTable() {
        try (Connection connection = sqlManager.getConnection()) {
            try (var statement = connection.createStatement()) {
                String createTableSql = """
                    CREATE TABLE IF NOT EXISTS player_homes (
                        uuid VARCHAR(36) NOT NULL,
                        server_id VARCHAR(255) NOT NULL,
                        home_number INT NOT NULL,
                        world_name VARCHAR(255) NOT NULL,
                        x DOUBLE NOT NULL,
                        y DOUBLE NOT NULL,
                        z DOUBLE NOT NULL,
                        yaw FLOAT NOT NULL,
                        pitch FLOAT NOT NULL,
                        PRIMARY KEY (uuid, home_number)
                    )
                    """;
                statement.executeUpdate(createTableSql);
            }
        } catch (SQLException exception) {
            YellowMCCoreV2.getInstance().getLogger().severe("Failed to create player_homes table: " + exception.getMessage());
        }
    }
}