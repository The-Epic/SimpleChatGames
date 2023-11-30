package xyz.epicebic.simplechatgames.games;

import lombok.Getter;
import xyz.epicebic.simplechatgames.SimpleChatGames;
import me.epic.chatgames.games.data.*;
import xyz.epicebic.simplechatgames.utils.CooldownManager;
import xyz.epicebic.simplechatgames.utils.Utils;
import me.epic.spigotlib.formatting.Formatting;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.epicebic.simplechatgames.games.data.*;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameManager implements Listener {
    private final List<GameData> games = new ArrayList<>();
    @Getter private final SimpleChatGames plugin;
    @Getter private long lastGameTime = System.currentTimeMillis();
    private boolean answerDelayed;
    private int answerDelayTime;
    private final CooldownManager cooldowns = new CooldownManager();

    @Getter private volatile ChatGame<? extends GameData> activeGame = null;

    public GameManager(SimpleChatGames plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        ConfigurationSection antiSpam = plugin.getAntiSpamConfig().getConfigurationSection("antispam");
        this.answerDelayed = antiSpam.getBoolean("delay.enabled", false);
        this.answerDelayTime = antiSpam.getInt("delay.delay", 3);
    }

    public void loadGames() {
        registerGame(new UnscrambleGameData(getAndSaveGameFile("unscramble.yml")));
        registerGame(new TriviaGameData(getAndSaveGameFile("trivia.yml")));
        registerGame(new CopyGameData(getAndSaveGameFile("copy.yml")));
        registerGame(new MathGameData(getAndSaveGameFile("maths.yml")));
        registerGame(new FillinGameData(getAndSaveGameFile("fill-the-blanks.yml")));
        registerGame(new QuizGameData(getAndSaveGameFile("quiz.yml")));
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

    public void reloadGames() {
        if (isGameRunning()) this.activeGame.end();
        for (GameData gameData : games) {
            gameData.reloadConfig();
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (activeGame == null) return;
        if (answerDelayed) {
            if (!cooldowns.hasCooldown(event.getPlayer().getUniqueId())) {
                activeGame.handleChat(event);
                cooldowns.setCooldown(event.getPlayer().getUniqueId(), Duration.ofSeconds(3));
                return;
            }
            event.getPlayer().sendMessage(Formatting.translate(plugin.getAntiSpamConfig().getString("antispam.delay.message")));
            return;
        }
        activeGame.handleChat(event);
    }

    private File getAndSaveGameFile(String name) {
        File gamesFolder = new File(plugin.getDataFolder(), "games");
        Utils.loadResourceFile(plugin, name, gamesFolder);
        return new File(gamesFolder, name);
    }
}