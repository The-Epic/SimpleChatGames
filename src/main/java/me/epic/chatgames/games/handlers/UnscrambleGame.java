package me.epic.chatgames.games.handlers;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.data.UnscrambleGameData;
import me.epic.chatgames.utils.Utils;
import me.epic.spigotlib.Timings;
import me.epic.spigotlib.formatting.Formatting;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UnscrambleGame extends ChatGame {

    private String answer = new String();
    private YamlConfiguration gameConfig = gameData.getGameConfig();

    public UnscrambleGame(UnscrambleGameData data, GameManager manager) {
        super(data.getDuration(), manager, data);
    }

    @Override
    protected void start() {
        super.start();
        // Start logic
        List<String> possibleAnswers = gameConfig.getStringList("words");
        this.answer = possibleAnswers.get(ThreadLocalRandom.current().nextInt(possibleAnswers.size()));

        Bukkit.broadcastMessage(Formatting.translate(gameConfig.getString("messages.start").replace("%word%", Utils.scrambleWord(answer))));
        if (manager.getPlugin().isDebugMode()) Bukkit.getOperators().forEach(offlinePlayer -> {
            if (offlinePlayer.isOnline()) {
                Bukkit.getPlayer(offlinePlayer.getName()).sendMessage("Chat Game Answer: " + answer);
            }
        });
        Timings.startTimings("unscramble-chatgame");
    }

    @Override
    protected void win(Player player) {
        super.win(player);
        // Win logic
        long timeTookLong = Timings.endTimings("unscramble-chatgame");
        StringBuilder finalTimeTook = new StringBuilder();
        finalTimeTook.append(String.format("%.2f", ((double) timeTookLong / 1000.0)));
        Utils.giveRewardAndNotify(manager.getPlugin(), player, gameData, finalTimeTook.toString());
        answer = new String();
    }

    @Override
    protected void end(boolean timeout) {
        super.end(timeout);
        // End logic
        if (timeout) {
            Bukkit.broadcastMessage(Formatting.translate(gameConfig.getString("messages.end.timed-out").replace("%word%", answer)));
        }
        answer = new String();
    }

    @Override
    public void handleChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().equals(answer)) {
            win(event.getPlayer());
        }
    }
}
