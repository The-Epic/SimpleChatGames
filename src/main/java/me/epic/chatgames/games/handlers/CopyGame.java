package me.epic.chatgames.games.handlers;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.data.CopyGameData;
import me.epic.chatgames.utils.RandomStringGenerator;
import me.epic.chatgames.utils.Utils;
import me.epic.spigotlib.Timings;
import me.epic.spigotlib.formatting.Formatting;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CopyGame extends ChatGame<CopyGameData> {


    private String answer = "";
    private final YamlConfiguration gameConfig;

    public CopyGame(CopyGameData data, GameManager manager) {
        super(data.getDuration(), manager, data);
        this.gameConfig = data.getGameConfig();
    }

    @Override
    protected void start() {
        super.start();

        if (gameConfig.getString("type", "auto-generated").equals("static")) {
            List<String> possibleAnswers = gameConfig.getStringList("copy-words");
            this.answer = possibleAnswers.get(ThreadLocalRandom.current().nextInt(possibleAnswers.size()));
        } else {
            this.answer = new RandomStringGenerator(gameConfig.getString("possible-characters")).generate(gameConfig.getInt("word-length"));
        }


        Bukkit.broadcastMessage(Formatting.translate(gameConfig.getString("messages.start").replace("%word%", answer)));

        super.sendDebugAnswer(this.answer);
        Timings.startTimings("copy-chatgame");
    }

    @Override
    protected void win(Player player) {
        super.win(player);

        Utils.giveRewardAndNotify(manager.getPlugin(), player, gameData, Timings.endTimings("copy-chatgame"));
        answer = "";
    }

    @Override
    protected void end(boolean timeout) {
        super.end(timeout);

        if (timeout) {
            Bukkit.broadcastMessage(Formatting.translate(gameConfig.getString("messages.end.timed-out")));
        }
        answer = "";
    }

    @Override
    public void handleChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().equals(answer)) {
            win(event.getPlayer());
        }
    }
}
