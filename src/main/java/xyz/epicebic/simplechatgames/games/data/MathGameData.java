package xyz.epicebic.simplechatgames.games.data;

import xyz.epicebic.simplechatgames.games.ChatGame;
import xyz.epicebic.simplechatgames.games.GameManager;
import xyz.epicebic.simplechatgames.games.handlers.MathGame;

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
