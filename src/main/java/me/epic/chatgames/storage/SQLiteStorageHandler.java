package me.epic.chatgames.storage;

import lombok.SneakyThrows;
import me.epic.chatgames.SimpleChatGames;
import me.epic.chatgames.utils.PlayerData;
import me.epic.spigotlib.storage.SQliteConnectionPool;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLiteStorageHandler implements StorageHandler {
    private Connection connection;

    public SQLiteStorageHandler() {
        SQliteConnectionPool connectionPool = new SQliteConnectionPool("SimpleChatGames", "data", SimpleChatGames.getPlugin().getDataFolder());
        this.connection = connectionPool.getConnection();

        createTable();
    }

    @SneakyThrows
    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_data (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "data INTEGER" +
                ");";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }


    @Override
    @SneakyThrows
    public int getPlayerData(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        int data = 0;
        String sql = "SELECT data FROM player_data WHERE uuid = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, uuid.toString());
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            data = rs.getInt("data");
        }
        return data;
    }

    @Override
    @SneakyThrows
    public void incrementPlayerData(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        int data = getPlayerData(player) + 1;
        String sql = "INSERT INTO player_data (uuid, data) VALUES (?, ?)" +
                "ON CONFLICT(uuid) DO UPDATE SET data = data + 1";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, uuid.toString());
        preparedStatement.setInt(2, data);
        preparedStatement.executeUpdate();
    }

    @Override
    @SneakyThrows
    public List<PlayerData> getTopPlayerData(int start, int count) {
        List<PlayerData> topPlayerData = new ArrayList<>();
        String sql = "SELECT uuid, data FROM player_data ORDER BY data DESC LIMIT ?, ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, start);
        preparedStatement.setInt(2, count);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("uuid")));
            int data = rs.getInt("data");
            topPlayerData.add(new PlayerData(player, data));
        }

        return topPlayerData;
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
