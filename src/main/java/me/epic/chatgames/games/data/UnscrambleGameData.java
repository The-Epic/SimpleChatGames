package me.epic.chatgames.games.data;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.handlers.UnscrambleGame;
import org.bukkit.configuration.file.YamlConfiguration;

public class UnscrambleGameData extends GameData {

    public UnscrambleGameData(YamlConfiguration config) {
        super(config);
    }

    @Override
    public ChatGame createGame(GameManager manager) {
        return new UnscrambleGame(this, manager);
    }
}
