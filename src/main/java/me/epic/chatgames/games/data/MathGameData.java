package me.epic.chatgames.games.data;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.handlers.MathGame;

import java.io.File;

public class MathGameData extends GameData {

    public MathGameData(File file) {
        super(file);
    }

    @Override
    public ChatGame<MathGameData> createGame(GameManager manager) {
        return new MathGame(this, manager);
    }
}
