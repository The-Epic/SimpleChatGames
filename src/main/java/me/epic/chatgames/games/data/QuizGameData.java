package me.epic.chatgames.games.data;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.handlers.QuizGame;

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
