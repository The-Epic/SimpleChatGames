package xyz.epicebic.simplechatgames.managers;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.epicebic.simplechatgames.SimpleChatGames;
import xyz.epicebic.simplechatgames.configs.GlobalConfig;
import xyz.epicebic.simplechatgames.configs.StorageConfig;
import xyz.epicebic.simplechatgames.storage.StorageHandler;
import xyz.epicebic.simplechatgames.storage.data.PlayerData;
import xyz.epicebic.simplechatgames.storage.impl.SQLiteStorageHandler;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.logging.Level.SEVERE;

public class StorageManager {

    private static final StorageManager INSTANCE = new StorageManager();

    private StorageHandler storageHandler;

    public void initData() {
        switch (StorageConfig.STORAGE_TYPE) {
            case SQLITE -> this.storageHandler = new SQLiteStorageHandler();
        }
        this.storageHandler.createTable().whenComplete((ignored, throwable) -> {
            if (throwable != null) {
                SimpleChatGames.getPlugin().getLogger().log(SEVERE, "Error while creating table", throwable);
                return;
            }
            this.loadLeaderboard();
        });
    }

    public CompletableFuture<PlayerData> loadPlayerData(UUID uuid) {
        return this.storageHandler.loadPlayerData(uuid).whenComplete((playerData, throwable) -> {
            if (throwable != null) {
                SimpleChatGames.getPlugin().getLogger()
                               .log(SEVERE, "Error while loading player data for " + uuid, throwable);
                return;
            }
            if (playerData != null) {
                this.store(uuid, playerData);
            }
        });
    }

    public CompletableFuture<Void> savePlayerData(PlayerData playerData) {
        return this.storageHandler.savePlayerData(playerData).whenComplete((ignored, throwable) -> {
            if (throwable != null) {
                SimpleChatGames.getPlugin().getLogger()
                               .log(SEVERE, "Error while saving player data for " + playerData.getUUID(), throwable);
                return;
            }
            this.store(playerData.getUUID(), playerData);
        });
    }

    public CompletableFuture<Void> updatePlayerData(PlayerData playerData) {
        return this.storageHandler.updatePlayerData(playerData).whenComplete((ignored, throwable) -> {
            if (throwable != null) {
                SimpleChatGames.getPlugin().getLogger()
                               .log(SEVERE, "Error while updating player data for " + playerData.getUUID(), throwable);
                return;
            }
            this.store(playerData.getUUID(), playerData);
        });
    }

    private void loadLeaderboard() {
        this.storageHandler.getLeaderboard().whenComplete((playerDataList, throwable) -> {
            if (throwable != null) {
                SimpleChatGames.getPlugin().getLogger().log(SEVERE, "Error while loading leaderboard", throwable);
                return;
            }
            for (PlayerData playerData : playerDataList) {
                this.store(playerData.getUUID(), playerData);
            }
        });
    }

    public void store(UUID key, PlayerData value) {
        DataManager.getInstance().store(key, value);
    }

    public static StorageManager getInstance() {
        return INSTANCE;
    }


}
