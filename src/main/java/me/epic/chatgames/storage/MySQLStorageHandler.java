package me.epic.chatgames.storage;

import lombok.Cleanup;
import lombok.SneakyThrows;
import me.epic.chatgames.utils.PlayerData;
import me.epic.spigotlib.storage.MySQLConnectionPool;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLStorageHandler implements StorageHandler{
    private MySQLConnectionPool connectionPool;

    public MySQLStorageHandler(String databaseName, String ip, String port, String username, String password) {
        this.connectionPool = new MySQLConnectionPool("SimpleChatGames", databaseName, ip, port, username, password);

        createTable();
    }

    @SneakyThrows
    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_data (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "data INTEGER" +
                ");";
        @Cleanup PreparedStatement statement = connectionPool.getConnection().prepareStatement(sql);
        statement.executeUpdate();
    }

    private CompletableFuture<Connection> getConnectionAsync() {
        return CompletableFuture.supplyAsync(() -> connectionPool.getConnection());
    }

    @Override
    public int getPlayerData(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        CompletableFuture<Integer> futureData = getConnectionAsync().thenApplyAsync(connection -> {
            try {
                String sql = "SELECT data FROM player_data WHERE uuid = ?;";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, uuid.toString());
                    ResultSet rs = preparedStatement.executeQuery();
                    if (rs.next()) {
                        return rs.getInt("data");
                    } else {
                        return 0;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        return futureData.join();
    }

    @Override
    public void incrementPlayerData(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        getConnectionAsync().thenApplyAsync(connection -> {
            try {
                String sql = "INSERT INTO player_data (uuid, data) VALUES (?, ?)" +
                        "ON DUPLICATE KEY UPDATE data = data + 1";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, uuid.toString());
                    preparedStatement.setInt(2, getPlayerData(player) + 1);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    @Override
    public List<PlayerData> getTopPlayerData(int start, int count) {
        CompletableFuture<List<PlayerData>> futureTopPlayerData = getConnectionAsync().thenApplyAsync(connection -> {
            List<PlayerData> topPlayerData = new ArrayList<>();
            try {
                String sql = "SELECT uuid, data FROM player_data ORDER BY data DESC LIMIT ?, ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, start);
                    preparedStatement.setInt(2, count);
                    try (ResultSet rs = preparedStatement.executeQuery()) {
                        while (rs.next()) {
                            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("uuid")));
                            int data = rs.getInt("data");
                            topPlayerData.add(new PlayerData(player, data));
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return topPlayerData;
        });
        return futureTopPlayerData.join();
    }

    @Override
    public PlayerData getPlayerDataAtPosition(int position) {
        List<PlayerData> topPlayerData = getTopPlayerData(position - 1, 1);
        if (topPlayerData.isEmpty()) {
            return PlayerData.ofUnknown();
        }
        return topPlayerData.get(0);
    }
}
