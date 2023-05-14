package me.epic.chatgames.games.data;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.handlers.CopyGame;
import org.bukkit.configuration.file.YamlConfiguration;

public class CopyGameData extends GameData {

    public CopyGameData(YamlConfiguration config ) {
        super(config);
    }

    @Override
    public ChatGame<CopyGameData> createGame(GameManager manager) {
        return new CopyGame(this, manager);
    }
}
