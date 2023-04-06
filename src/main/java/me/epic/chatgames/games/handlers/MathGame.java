package me.epic.chatgames.games.handlers;

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.data.MathGameData;
import me.epic.chatgames.utils.MathQuestionGenerator;
import me.epic.chatgames.utils.Utils;
import me.epic.spigotlib.Timings;
import me.epic.spigotlib.formatting.Formatting;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MathGame extends ChatGame<MathGameData> {

    private String answer = new String();
    private final YamlConfiguration gameConfig = gameData.getGameConfig();


    public MathGame(MathGameData data, GameManager manager) {
        super(data.getDuration(), manager, data);
    }

    @Override
    protected void start() {
        super.start();


        MathQuestionGenerator mathQuestionGenerator = new MathQuestionGenerator(gameConfig.getInt("numbers.lowest", 1), gameConfig.getInt("numbers.highest", 200));
        String question = mathQuestionGenerator.generateQuestion();
        this.answer = String.valueOf(mathQuestionGenerator.getResult());

        Bukkit.broadcastMessage(Formatting.translate(gameConfig.getString("messages.start").replace("%question%", question)));
        if (manager.getPlugin().isDebugMode()) Bukkit.getOperators().forEach(offlinePlayer -> {
            if (offlinePlayer.isOnline()) {
                Bukkit.getPlayer(offlinePlayer.getName()).sendMessage("Chat Game Answer: " + answer);
            }
        });
        Timings.startTimings("math-chatgame");

    }

    @Override
    protected void win(Player player) {
        super.win(player);

        long timeTookLong = Timings.endTimings("math-chatgame");
        String finalTimeTook = String.format("%.2f", ((double) timeTookLong / 1000.0));
        Utils.giveRewardAndNotify(manager.getPlugin(), player, gameData, finalTimeTook);
        this.answer = new String();
    }

    @Override
    protected void end(boolean timeout) {
        super.end(timeout);

        if (timeout) {
            Bukkit.broadcastMessage(Formatting.translate(gameConfig.getString("messages.end.timed-out").replace("%answer%", answer)));
        }
        this.answer = new String();
    }

    @Override
    public void handleChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().equals(answer)) {
            win(event.getPlayer());
        }
    }
}
