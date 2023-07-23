package me.epic.chatgames.games.data;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.handlers.FillinGame;
import org.bukkit.configuration.file.YamlConfiguration;

public class FillinGameData extends GameData {

    public FillinGameData(YamlConfiguration config) {
        super(config);
    }

    @Override
    public ChatGame<FillinGameData> createGame(GameManager manager) {
        return new FillinGame(manager, this);
    }
}
