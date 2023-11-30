package xyz.epicebic.simplechatgames.games.data;

import xyz.epicebic.simplechatgames.games.ChatGame;
import xyz.epicebic.simplechatgames.games.GameManager;
import xyz.epicebic.simplechatgames.games.handlers.TriviaGame;
import xyz.epicebic.simplechatgames.utils.Utils;

import java.io.File;

public class TriviaGameData extends GameData {

    public TriviaGameData(File file) {
        super(Utils.updateQuestions(file));
    }

    @Override
    public ChatGame<TriviaGameData> createGame(GameManager manager) {
        return new TriviaGame(this, manager);
    }
}
