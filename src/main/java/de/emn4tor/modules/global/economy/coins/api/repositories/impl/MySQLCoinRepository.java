package de.emn4tor.modules.global.economy.coins.api.repositories.impl;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.global.economy.coins.api.repositories.CoinRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

@RequiredArgsConstructor
public final class MySQLCoinRepository implements CoinRepository {
    private final Connection connection;

    @Override
    public void setupRepository() {
        var statement = "CREATE TABLE IF NOT EXISTS player_coins (uuid VARCHAR(36) PRIMARY KEY, coins INT)";

        try (var preparedStatement = this.connection.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            YellowMCCoreV2.getInstance().getLogger().severe(sqlException.getMessage());
        }
    }

    @Override
    public int findCoinsByUuid(@NotNull UUID uuid) {
        var statement = "SELECT * FROM player_coins WHERE uuid = ?";

        try (var preparedStatement = this.connection.prepareStatement(statement)) {
            preparedStatement.setString(1, uuid.toString());

            var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("coins");
            }
        } catch (SQLException sqlException) {
            YellowMCCoreV2.getInstance().getLogger().severe(sqlException.getMessage());
        }

        return -1;
    }

    @Override
    public void addCoinsByUuid(@NotNull UUID uuid, int coins) {
        var statement = "INSERT INTO player_coins (uuid, coins) VALUES (?,?) ON DUPLICATE KEY UPDATE coins = coins + VALUES(coins)";

        try (var preparedStatement = this.connection.prepareStatement(statement)) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, coins);

            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            YellowMCCoreV2.getInstance().getLogger().severe(sqlException.getMessage());
        }
    }

    @Override
    public void setCoinsByUuid(@NotNull UUID uuid, int coins) {
        var statement = "INSERT INTO player_coins (uuid, coins) VALUES (?,?) ON DUPLICATE KEY UPDATE coins = VALUES(coins)";

        try (var preparedStatement = this.connection.prepareStatement(statement)) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, coins);

            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            YellowMCCoreV2.getInstance().getLogger().severe(sqlException.getMessage());
        }
    }

    @Override
    public void removeCoinsByUuid(@NotNull UUID uuid, int coins) {
        var statement = "UPDATE player_coins SET coins = coins - ? WHERE uuid = ?";

        try (var preparedStatement = this.connection.prepareStatement(statement)) {
            preparedStatement.setInt(1, coins);
            preparedStatement.setString(2, uuid.toString());

            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            YellowMCCoreV2.getInstance().getLogger().severe(sqlException.getMessage());
        }
    }
}
