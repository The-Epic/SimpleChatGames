package me.epic.chatgames.games.data;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.handlers.UnscrambleGame;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class UnscrambleGameData extends GameData {

    public UnscrambleGameData(File gameConfigFile) {
        super(gameConfigFile);
    }

    @Override
    public ChatGame<UnscrambleGameData> createGame(GameManager manager) {
        return new UnscrambleGame(this, manager);
    }
}
