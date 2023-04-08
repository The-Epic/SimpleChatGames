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
import java.sql.Statement;
import java.util.*;

public class SQLiteStorageHandler implements StorageHandler {
    private Connection connection;
    private final Map<UUID, Integer> dataMap = new HashMap<>();

    public SQLiteStorageHandler() {
        SQliteConnectionPool connectionPool = new SQliteConnectionPool("SimpleChatGames", "data", SimpleChatGames.getPlugin().getDataFolder());
        this.connection = connectionPool.getConnection();

        createTable();
        loadData();
    }

    @SneakyThrows
    private void loadData() {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid, data FROM player_data");
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            UUID uuid = UUID.fromString(rs.getString("uuid"));
            int data = rs.getInt("data");
            dataMap.put(uuid, data);
        }
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
        dataMap.put(uuid, data);
    }

    @Override
    public List<PlayerData> getTopPlayerData(int start, int count) {
        List<PlayerData> topPlayerData = new ArrayList<>();
        List<Map.Entry<UUID, Integer>> sortedEntries = new ArrayList<>(dataMap.entrySet());
        sortedEntries.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));
        int endIndex = Math.min(start + count, sortedEntries.size());
        for (int i = start; i < endIndex; i++) {
            Map.Entry<UUID, Integer> entry = sortedEntries.get(i);
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            int data = entry.getValue();
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
