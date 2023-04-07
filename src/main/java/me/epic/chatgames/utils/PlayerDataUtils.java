package me.epic.chatgames.utils;

import lombok.SneakyThrows;
import me.epic.chatgames.storage.JsonStorageHandler;
import me.epic.chatgames.storage.SQLiteStorageHandler;
import me.epic.chatgames.storage.StorageHandler;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerDataUtils {

    private static StorageHandler storageHandler;

    public static void init(String storageType) {
        switch (storageType.toLowerCase()) {
            case "json" -> storageHandler = new JsonStorageHandler();
            case "sqlite" -> storageHandler = new SQLiteStorageHandler();
            default -> throw new IllegalArgumentException("Incorrect storage type");
        }
    }

    @SneakyThrows
    public static int getPlayerData(OfflinePlayer player) {
        return storageHandler.getPlayerData(player);
    }

    @SneakyThrows
    public static void incrementPlayerData(Player player) {
        storageHandler.incrementPlayerData(player);
    }

    @SneakyThrows
    public static List<PlayerData> getTopPlayerData(int start, int count) {
        return storageHandler.getTopPlayerData(start, count);
    }


    public static PlayerData getPlayerDataAtPosition(int position) {
        return storageHandler.getPlayerDataAtPosition(position);
    }
}