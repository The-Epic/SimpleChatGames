package me.epic.chatgames.games.data;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.handlers.TriviaGame;
import org.bukkit.configuration.file.YamlConfiguration;

public class TriviaGameData extends GameData {

    public TriviaGameData(YamlConfiguration config) {
        super(config);
    }

    @Override
    public ChatGame createGame(GameManager manager) {
        return new TriviaGame(this, manager);
    }
}
