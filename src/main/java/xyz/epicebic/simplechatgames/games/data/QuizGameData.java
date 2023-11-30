package xyz.epicebic.simplechatgames.games.data;

import xyz.epicebic.simplechatgames.games.ChatGame;
import xyz.epicebic.simplechatgames.games.GameManager;
import xyz.epicebic.simplechatgames.games.handlers.QuizGame;

import java.io.File;

public class QuizGameData extends GameData {
    public QuizGameData(File gameConfigFile) {
        super(gameConfigFile);
    }

    @Override
    public ChatGame<QuizGameData> createGame(GameManager manager) {
        return new QuizGame(this, manager);
    }
}
