package xyz.epicebic.simplechatgames.utils;

import lombok.SneakyThrows;
import xyz.epicebic.simplechatgames.SimpleChatGames;
import me.epic.chatgames.storage.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import xyz.epicebic.simplechatgames.storage.AsyncStorageHandler;
import xyz.epicebic.simplechatgames.storage.JsonStorageHandler;
import xyz.epicebic.simplechatgames.storage.MySQLStorageHandler;
import xyz.epicebic.simplechatgames.storage.SQLiteStorageHandler;

import java.util.*;
import java.util.concurrent.Executors;

public class PlayerDataUtils {

    private static AsyncStorageHandler asyncStorageHandler;

    public static void init(String storageType) {
        switch (storageType.toLowerCase()) {
            case "json" -> asyncStorageHandler = new AsyncStorageHandler(Executors.newSingleThreadExecutor(), new JsonStorageHandler());
            case "sqlite" -> asyncStorageHandler = new AsyncStorageHandler(Executors.newSingleThreadExecutor(), new SQLiteStorageHandler());
            case "mysql" -> {
                ConfigurationSection mysql = SimpleChatGames.getPlugin().getConfig().getConfigurationSection("storage.mysql");
                asyncStorageHandler = new AsyncStorageHandler(Executors.newSingleThreadExecutor(), new MySQLStorageHandler(
                        mysql.getString("db-name"), mysql.getString("ip"), mysql.getString("port"),
                        mysql.getString("username"), mysql.getString("password")
                ));
            }
            default -> throw new IllegalArgumentException("Incorrect storage type");
        }
    }

    @SneakyThrows
    public static int getPlayerData(OfflinePlayer player) {
        return asyncStorageHandler.getPlayerData(player);
    }

    @SneakyThrows
    public static void incrementPlayerData(Player player) {
        asyncStorageHandler.incrementPlayerData(player);
    }

    @SneakyThrows
    public static List<PlayerData> getTopPlayerData(int start, int count) {
        return asyncStorageHandler.getTopPlayerData(start, count);
    }


    public static PlayerData getPlayerDataAtPosition(int position) {
        return asyncStorageHandler.getPlayerDataAtPosition(position);
    }
}