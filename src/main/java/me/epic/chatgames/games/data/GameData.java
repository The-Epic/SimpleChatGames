package me.epic.chatgames.games.data;

import lombok.Getter;
import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class GameData {

    private final int duration;
    private final File gameFile;
    @Getter private YamlConfiguration gameConfig;

    public GameData(File gameConfigFile) {
        this.gameFile = gameConfigFile;
        this.gameConfig = YamlConfiguration.loadConfiguration(this.gameFile);
        this.duration = gameConfig.getInt("duration");
    }

    public void reloadConfig() {
        this.gameConfig = YamlConfiguration.loadConfiguration(this.gameFile);
    }

    public abstract ChatGame<?> createGame(GameManager manager);

    public int getDuration() {
        return duration;
    }
}