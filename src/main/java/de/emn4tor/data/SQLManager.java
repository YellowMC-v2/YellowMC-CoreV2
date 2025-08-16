package de.emn4tor.data;

/*
 *  @author: Emn4tor
 *  @created: 24.07.2025
 */

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.emn4tor.YellowMCCoreV2;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLManager {
    private static SQLManager instance;
    private final HikariDataSource dataSource;

    private SQLManager(FileConfiguration config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getString("credentials.database.url"));
        hikariConfig.setUsername(config.getString("credentials.database.user"));
        hikariConfig.setPassword(config.getString("credentials.database.password"));

        hikariConfig.setMaximumPoolSize(config.getInt("hikari.maximumPoolSize", 10));
        hikariConfig.setMinimumIdle(config.getInt("hikari.minimumIdle", 2));
        hikariConfig.setConnectionTimeout(config.getInt("hikari.connectionTimeout", 30000));
        hikariConfig.setIdleTimeout(config.getInt("hikari.idleTimeout", 600000));
        hikariConfig.setLeakDetectionThreshold(config.getInt("hikari.leakDetectionThreshold", 2000));

        dataSource = new HikariDataSource(hikariConfig);
    }

    public static void init(FileConfiguration config) {
        if (instance == null) {
            instance = new SQLManager(config);
        }
    }

    public static SQLManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SQLManager not initialized, call init() first.");
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
