package xyz.epicebic.simplechatgames.storage;

import lombok.Cleanup;
import lombok.SneakyThrows;
import xyz.epicebic.simplechatgames.SimpleChatGames;
import xyz.epicebic.simplechatgames.utils.PlayerData;
import me.epic.spigotlib.storage.MySQLConnectionPool;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MySQLStorageHandler implements StorageHandler {

    private final MySQLConnectionPool connectionPool;
    private final Map<UUID, Integer> dataMap = new HashMap<>();

    public MySQLStorageHandler(String dbName, String ip, String port, String username, String password) {
        connectionPool = new MySQLConnectionPool("SimpleChatGames", dbName, ip, port, username, password);

        createTable();
        loadData();
    }

    private void loadData() {
        Bukkit.getScheduler().runTaskAsynchronously(SimpleChatGames.getPlugin(), () -> {
            try {
                @Cleanup PreparedStatement preparedStatement = connectionPool.getConnection().prepareStatement("SELECT uuid, data FROM player_data");
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    int data = rs.getInt("data");
                    dataMap.put(uuid, data);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

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


    @Override
    @SneakyThrows
    public int getPlayerData(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        return dataMap.getOrDefault(uuid, 0);
    }

    @Override
    @SneakyThrows
    public void incrementPlayerData(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        int data = getPlayerData(player) + 1;
        String sql = "INSERT INTO player_data (uuid, data) VALUES (?, ?)" +
                "ON CONFLICT(uuid) DO UPDATE SET data = data + 1";
        @Cleanup PreparedStatement preparedStatement = connectionPool.getConnection().prepareStatement(sql);
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
