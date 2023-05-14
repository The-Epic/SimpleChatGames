package me.epic.chatgames.games.handlers;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.data.TriviaGameData;
import me.epic.chatgames.utils.Utils;
import me.epic.spigotlib.Timings;
import me.epic.spigotlib.formatting.Formatting;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class TriviaGame extends ChatGame<TriviaGameData> {
    private List<String> answers = new ArrayList<>();
    private final YamlConfiguration gameConfig = gameData.getGameConfig();
    private final List<Map<String, List<String>>> mapList;

    public TriviaGame(TriviaGameData data, GameManager manager) {
        super(data.getDuration(), manager, data);
        mapList = Utils.fixList(gameConfig.getMapList("questions"));
    }

    @Override
    protected void start() {
        super.start();

        List<String> questions = mapList.stream().map(map -> map.get("question").get(0)).toList();
        String question = questions.get(ThreadLocalRandom.current().nextInt(questions.size()));
        for (Map<String, List<String>> map : mapList) {
            if (map.get("question").get(0).equals(question)) {
                answers = map.get("answers").stream().map(String::trim).map(String::valueOf).toList();
                break;
            }
        }
        Bukkit.broadcastMessage(Formatting.translate(gameConfig.getString("messages.start").replace("%question%", question)));
        if (manager.getPlugin().isDebugMode()) Bukkit.getOperators().forEach(offlinePlayer -> {
            if (offlinePlayer.isOnline()) {
                Bukkit.getPlayer(offlinePlayer.getName()).sendMessage("Chat Game Answer: " + Utils.formatListAnswers(answers));
            }
        });
        Timings.startTimings("trivia-chatgame");
    }

    @Override
    protected void win(Player player) {
        super.win(player);

        long timeTookLong = Timings.endTimings("trivia-chatgame");
        String finalTimeTook = String.format("%.2f", ((double) timeTookLong / 1000.0));
        Utils.giveRewardAndNotify(manager.getPlugin(), player, gameData, finalTimeTook);
        answers = new ArrayList<>();
    }

    @Override
    protected void end(boolean timeout) {
        super.end(timeout);
        if (timeout) {
            Bukkit.broadcastMessage(Formatting.translate(gameConfig.getString("messages.end.timed-out").replace("%answers%", Utils.formatListAnswers(answers))));
        }
        answers = new ArrayList<>();
    }

    @Override
    public void handleChat(AsyncPlayerChatEvent event) {
        String playerMessage = event.getMessage();
        for (String possibleAnswer : answers) {
            if (possibleAnswer.equalsIgnoreCase(playerMessage)) {
                win(event.getPlayer());
                break;
            }
        }
    }
}
