package me.epic.chatgames.games.data;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.handlers.TriviaGame;
import me.epic.chatgames.utils.Utils;

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
