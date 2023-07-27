package me.epic.chatgames.games.data;

import lombok.Getter;
import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class GameData {

    private final int duration;
    @Getter private YamlConfiguration gameConfig;

    public GameData(YamlConfiguration config) {
        this.gameConfig = config;
        this.duration = config.getInt("duration");
    }

//    public GameData(File gameConfigFile) {
//
//    }

    public abstract ChatGame<?> createGame(GameManager manager);

    public int getDuration() {
        return duration;
    }
}