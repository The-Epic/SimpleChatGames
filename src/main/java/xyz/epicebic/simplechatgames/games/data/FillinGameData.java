package xyz.epicebic.simplechatgames.games.data;

import xyz.epicebic.simplechatgames.games.ChatGame;
import xyz.epicebic.simplechatgames.games.GameManager;
import xyz.epicebic.simplechatgames.games.handlers.FillinGame;

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
