package me.epic.chatgames.games.data;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.handlers.FillinGame;

import java.io.File;

public class FillinGameData extends GameData {

    public FillinGameData(File file) {
        super(file);
    }

    @Override
    public ChatGame<FillinGameData> createGame(GameManager manager) {
        return new FillinGame(manager, this);
    }
}
