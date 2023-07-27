package me.epic.chatgames.games;

import lombok.Getter;
import me.epic.chatgames.SimpleChatGames;
import me.epic.chatgames.games.data.*;
import me.epic.chatgames.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameManager implements Listener {
    private final List<GameData> games = new ArrayList<>();
    @Getter private final SimpleChatGames plugin;
    @Getter private long lastGameTime = System.currentTimeMillis();

    @Getter private volatile ChatGame<? extends GameData> activeGame = null;

    public GameManager(SimpleChatGames plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void loadGames() {
        games.clear();
        Utils.loadResource(plugin, "unscramble.yml").ifPresent(config -> registerGame(new UnscrambleGameData(config)));
        Utils.loadResource(plugin, "trivia.yml").ifPresent(config -> registerGame(new TriviaGameData(config, "trivia.yml")));
        Utils.loadResource(plugin, "copy.yml").ifPresent(config -> registerGame(new CopyGameData(config)));
        Utils.loadResource(plugin, "maths.yml").ifPresent(config -> registerGame(new MathGameData(config)));
        Utils.loadResource(plugin, "fill-the-blanks.yml").ifPresent(config -> registerGame(new FillinGameData(config)));
    }

    public boolean isGameRunning() {
        return activeGame != null;
    }


    public void startRandomGame() {
        ChatGame<?> game = games.get(ThreadLocalRandom.current().nextInt(games.size())).createGame(this);
        if (!isGameRunning()) {
            activeGame = game;

            activeGame.start();
            this.lastGameTime = activeGame.getEndTime();
        }
    }

    public void clearActiveGame() {
        activeGame = null;
    }

    public void registerGame(GameData data) {
        if (data.getGameConfig().getBoolean("enabled")) {
            games.add(data);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (activeGame == null) return;
        activeGame.handleChat(event);
    }


}