package me.epic.chatgames.games;

import lombok.Getter;
import me.epic.chatgames.SimpleChatGames;
import me.epic.chatgames.Utils;
import me.epic.chatgames.games.data.GameData;
import me.epic.chatgames.games.data.UnscrambleGameData;
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

    @Getter private volatile ChatGame<? extends GameData> activeGame = null;

    public GameManager(SimpleChatGames plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void loadGames() {
        games.clear();
        Utils.loadResourceFile(plugin, "unscramble.yml").ifPresent(config -> registerGame(new UnscrambleGameData(config)));
    }


    public void startRandomGame() {
        ChatGame<? extends GameData> game = games.get(ThreadLocalRandom.current().nextInt(games.size())).createGame(this);
        activeGame = game;

        activeGame.start();
    }

    public void clearActiveGame() {
        activeGame = null;
    }

    private void registerGame(GameData data) {
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