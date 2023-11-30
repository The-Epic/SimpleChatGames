package xyz.epicebic.simplechatgames.games.data;

import xyz.epicebic.simplechatgames.games.ChatGame;
import xyz.epicebic.simplechatgames.games.GameManager;
import xyz.epicebic.simplechatgames.games.handlers.UnscrambleGame;

import java.io.File;

public class UnscrambleGameData extends GameData {

    public UnscrambleGameData(File gameConfigFile) {
        super(gameConfigFile);
    }

    @Override
    public ChatGame<UnscrambleGameData> createGame(GameManager manager) {
        return new UnscrambleGame(this, manager);
    }
}
