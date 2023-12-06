package xyz.epicebic.simplechatgames.storage;


import xyz.epicebic.simplechatgames.storage.data.PlayerData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageHandler {


    CompletableFuture<Void> createTable();

    CompletableFuture<PlayerData> loadPlayerData(UUID uuid);

    CompletableFuture<Void> savePlayerData(PlayerData playerData);

    CompletableFuture<Void> updatePlayerData(PlayerData playerData);

    CompletableFuture<List<PlayerData>> getLeaderboard();


}
