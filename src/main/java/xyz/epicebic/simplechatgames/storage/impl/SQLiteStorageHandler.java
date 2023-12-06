package xyz.epicebic.simplechatgames.storage.impl;

import xyz.epicebic.simplechatgames.configs.GlobalConfig;
import xyz.epicebic.simplechatgames.configs.StorageConfig;
import xyz.epicebic.simplechatgames.storage.StorageHandler;
import xyz.epicebic.simplechatgames.storage.connection.SQLConnectionPool;
import xyz.epicebic.simplechatgames.storage.data.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLiteStorageHandler implements StorageHandler {

    private final SQLConnectionPool connectionPool = new SQLConnectionPool();

    @Override
    public CompletableFuture<Void> createTable() {
        return CompletableFuture.runAsync(() -> {
            try (Connection connect = this.connectionPool.getConnection();
                 PreparedStatement statement = connect.prepareStatement(
                         "CREATE TABLE IF NOT EXISTS player_data (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                 "uuid CHAR(36) UNIQUE, wins INTEGER, " +
                                 "losses INTEGER, unclaimed_rewards INTEGER)")) {
                statement.executeQuery();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public CompletableFuture<PlayerData> loadPlayerData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connect = this.connectionPool.getConnection();
                 PreparedStatement statement = connect.prepareStatement("SELECT * FROM player_data WHERE uuid = ?")) {
                statement.setString(1, uuid.toString());
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    return new PlayerData(uuid, rs.getInt("wins"), rs.getInt("losses"), rs.getInt("unclaimed_rewards"));
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException("Something broke loading player data for " + uuid);
        });
    }

    @Override
    public CompletableFuture<Void> savePlayerData(PlayerData playerData) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connect = this.connectionPool.getConnection();
                 PreparedStatement statement = connect.prepareStatement("INSERT INTO player_data (uuid, wins, losses, unclaimed_rewards) VALUES (?, ?, ?, ?)")) {
                statement.setString(1, playerData.getUUID().toString());
                statement.setInt(2, playerData.getWins());
                statement.setInt(3, playerData.getLosses());
                statement.setInt(4, playerData.getUnclaimedRewards());
                statement.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public CompletableFuture<Void> updatePlayerData(PlayerData playerData) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connect = this.connectionPool.getConnection();
                 PreparedStatement statement = connect.prepareStatement("UPDATE player_data SET wins = ?, losses = ?, unclaimed_rewards = ? WHERE uuid = ?")) {
                statement.setInt(1, playerData.getWins());
                statement.setInt(2, playerData.getLosses());
                statement.setInt(3, playerData.getUnclaimedRewards());
                statement.setString(4, playerData.getUUID().toString());
                statement.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public CompletableFuture<List<PlayerData>> getLeaderboard() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connect = this.connectionPool.getConnection();
                 PreparedStatement statement = connect.prepareStatement("SELECT * FROM player_data ORDER BY wins DESC")) {
                ResultSet rs = statement.executeQuery();
                List<PlayerData> playerDataList = new ArrayList<>();
                while (rs.next()) {
                    playerDataList.add(new PlayerData(UUID.fromString(rs.getString("uuid")), rs.getInt("wins"), rs.getInt("losses"), rs.getInt("unclaimed_rewards")));
                }
                return playerDataList;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
