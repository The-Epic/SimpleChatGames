package me.epic.chatgames.games.data;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.handlers.MathGame;
import org.bukkit.configuration.file.YamlConfiguration;

public class MathGameData extends GameData {

    public MathGameData(YamlConfiguration config) {
        super(config);
    }

    @Override
    public ChatGame createGame(GameManager manager) {
        return new MathGame(this, manager);
    }
}
