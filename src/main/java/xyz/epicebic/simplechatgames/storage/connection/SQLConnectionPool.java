package xyz.epicebic.simplechatgames.storage.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import xyz.epicebic.simplechatgames.SimpleChatGames;
import xyz.epicebic.simplechatgames.configs.StorageConfig;
import xyz.epicebic.simplechatgames.storage.StorageType;
import xyz.epicebic.simplechatgames.storage.impl.SQLiteStorageHandler;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLConnectionPool {

    private HikariDataSource dataSource = null;

    public SQLConnectionPool() {

    }

    public void setupConnection() {
        String url;

        HikariConfig config = new HikariConfig();
        StorageType type = StorageConfig.STORAGE_TYPE;

        if (type == StorageType.SQLITE) {
            url = "jdbc:sqlite:" + SimpleChatGames.getPlugin().getDataFolder().getAbsolutePath() + "/database.db";
        } else {
            url = "jdbc:" + type.toString().toLowerCase() + "://" + StorageConfig.DATABASE_HOST + ":" + StorageConfig.DATABASE_PORT + "/" + StorageConfig.DATABASE_NAME;

            config.setUsername(StorageConfig.DATABASE_USERNAME);
            config.setPassword(StorageConfig.DATABASE_PASSWORD);
        }

        config.setJdbcUrl(url);
        config.setMaximumPoolSize(15);
        config.setPoolName("SimpleChatGames-Connection-Pool");

        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("characterEncoding", "utf-8");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("tcpKeepAlive", "true");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        config.addDataSourceProperty("useSSL", "false");
        config.addDataSourceProperty("verifyServerCertificate", "false");

        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = this.dataSource.getConnection();
        } catch (SQLException PANIC) {
            // PANIC
            PANIC.printStackTrace();
        }
        return connection;
    }

    public void close() {
        if (this.dataSource != null && !this.dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
