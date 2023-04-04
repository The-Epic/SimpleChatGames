package me.epic.chatgames.utils;

import lombok.Getter;
import org.bukkit.OfflinePlayer;

public class PlayerData {
    @Getter private String playerName;
    @Getter private int gamesWon;

    public PlayerData(OfflinePlayer player, int gamesWon) {
        if (player == null) {
            this.playerName = "Unknown";
        }  else {
            this.playerName = player.getName();
        }
        this.gamesWon = gamesWon;
    }

    public static PlayerData ofUnknown() {
        return new PlayerData(null, 0);
    }

    @Override
    public String toString() {
        return playerName + " " + gamesWon;
    }
}
