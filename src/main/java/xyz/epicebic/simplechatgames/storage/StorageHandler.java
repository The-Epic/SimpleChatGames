package xyz.epicebic.simplechatgames.storage;

import xyz.epicebic.simplechatgames.utils.PlayerData;
import org.bukkit.OfflinePlayer;

import java.util.List;

public interface StorageHandler {

    int getPlayerData(OfflinePlayer player);
    void incrementPlayerData(OfflinePlayer player);
    List<PlayerData> getTopPlayerData(int start, int count);
    PlayerData getPlayerDataAtPosition(int position);
}
