package me.epic.chatgames.storage;

import lombok.SneakyThrows;
import me.epic.chatgames.utils.PlayerData;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public interface StorageHandler {

    int getPlayerData(OfflinePlayer player);
    void incrementPlayerData(OfflinePlayer player);
    List<PlayerData> getTopPlayerData(int start, int count);
    PlayerData getPlayerDataAtPosition(int position);
}
