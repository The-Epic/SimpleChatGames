package me.epic.chatgames.games.handlers;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.data.MathGameData;
import me.epic.chatgames.games.data.QuizGameData;
import me.epic.chatgames.utils.Utils;
import me.epic.spigotlib.Timings;
import me.epic.spigotlib.formatting.Formatting;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;

public class QuizGame extends ChatGame<QuizGameData> {

    private final YamlConfiguration gameConfig;
    private String answer = "";
    private final List<Map<?, ?>> mapList;

    public QuizGame(QuizGameData data, GameManager manager) {
        super(data.getDuration(), manager, data);
        this.gameConfig = data.getGameConfig();
        this.mapList = gameConfig.getMapList("questions");
    }

    @SuppressWarnings("all")
    @Override
    protected void start() {
        super.start();

        Map<?, ?> randomChoice = mapList.get(ThreadLocalRandom.current().nextInt(mapList.size()));

        String question = (String) randomChoice.get("question");
        this.answer = (String) randomChoice.get("answer");
        Map<String, String> results = ((Map<String, String>) randomChoice.get("results"));

        StringJoiner joiner = new StringJoiner("\n");

        joiner.add(Formatting.translate(gameConfig.getString("messages.start").replace("%question%", question)));
        for (Map.Entry<String, String> entry : results.entrySet()) {
            joiner.add(Formatting.translate("<green>" + entry.getKey() + "</green>. <white>" + entry.getValue()));
        }
        Bukkit.broadcastMessage(joiner.toString());
        super.sendDebugAnswer(this.answer);
        Timings.startTimings("quiz-chatgame");
    }

    @Override
    protected void win(Player player) {
        super.win(player);

        Utils.giveRewardAndNotify(manager.getPlugin(), player, gameData, Timings.endTimings("quiz-chatgame"));
        this.answer = "";
    }

    @Override
    protected void end(boolean timeout) {
        super.end(timeout);

        if (timeout) {
            Bukkit.broadcastMessage(Formatting.translate(gameConfig.getString("messages.end.timed-out").replace("%answer%", answer)));
        }
        this.answer = "";
    }

    @Override
    public void handleChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().toLowerCase().trim().equalsIgnoreCase(answer)) {
            win(event.getPlayer());
        }
    }
}
