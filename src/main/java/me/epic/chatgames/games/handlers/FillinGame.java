package me.epic.chatgames.games.handlers;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.data.FillinGameData;
import me.epic.chatgames.utils.Utils;
import me.epic.spigotlib.Timings;
import me.epic.spigotlib.formatting.Formatting;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FillinGame extends ChatGame<FillinGameData> {

    private String answer = "";
    private final YamlConfiguration gameConfig;
    private double blankageAmount;

    public FillinGame(GameManager manager, FillinGameData data) {
        super(data.getDuration(), manager, data);
        this.gameConfig = data.getGameConfig();
        this.blankageAmount = Double.parseDouble(gameConfig.getString("how-much-blanked", "80%").substring(0, 1)) / 10;
    }

    @Override
    protected void start() {
        super.start();

        List<String> possibleQuestions = gameConfig.getStringList("questions");
        this.answer = possibleQuestions.get(ThreadLocalRandom.current().nextInt(possibleQuestions.size()));

        String question = Utils.addBlanks(this.answer, blankageAmount);
        Bukkit.broadcastMessage(Formatting.translate(gameConfig.getString("messages.start").replace("%word%", question)));

        if (manager.getPlugin().isDebugMode()) Bukkit.getOperators().forEach(offlinePlayer -> {
            if (offlinePlayer.isOnline()) {
                Bukkit.getPlayer(offlinePlayer.getName()).sendMessage("Chat Game Answer: " + answer);
            }
        });
        Timings.startTimings("fillin-chatgame");
    }

    @Override
    protected void win(Player player) {
        super.win(player);

        long timeTookLong = Timings.endTimings("fillin-chatgame");
        String finalTimeTook = String.format("%.2f", ((double) timeTookLong / 1000.0));
        Utils.giveRewardAndNotify(manager.getPlugin(), player, gameData, finalTimeTook);
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
    public void handleChat(@NotNull AsyncPlayerChatEvent event) {
        boolean caseInsensitive = gameConfig.getBoolean("case-insensitive");
        String input = event.getMessage().trim();
        if (caseInsensitive ? input.equals(answer) : input.equalsIgnoreCase(answer)) {
            win(event.getPlayer());
        }
    }
}
