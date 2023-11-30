package xyz.epicebic.simplechatgames.games.data;

import xyz.epicebic.simplechatgames.games.ChatGame;
import xyz.epicebic.simplechatgames.games.GameManager;
import xyz.epicebic.simplechatgames.games.handlers.CopyGame;

import java.io.File;

public class CopyGameData extends GameData {

    public CopyGameData(File file) {
        super(file);
    }

    @Override
    public ChatGame<CopyGameData> createGame(GameManager manager) {
        return new CopyGame(this, manager);
    }
}
