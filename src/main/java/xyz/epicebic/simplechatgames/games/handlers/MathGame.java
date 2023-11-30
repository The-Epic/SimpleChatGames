package xyz.epicebic.simplechatgames.games.handlers;

import xyz.epicebic.simplechatgames.games.ChatGame;
import xyz.epicebic.simplechatgames.games.GameManager;
import xyz.epicebic.simplechatgames.games.data.MathGameData;
import xyz.epicebic.simplechatgames.utils.MathQuestionGenerator;
import xyz.epicebic.simplechatgames.utils.Utils;
import me.epic.spigotlib.Timings;
import me.epic.spigotlib.formatting.Formatting;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MathGame extends ChatGame<MathGameData> {

    private String answer = "";
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
        super.sendDebugAnswer(this.answer);
        Timings.startTimings("math-chatgame");

    }

    @Override
    protected void win(Player player) {
        super.win(player);

        Utils.giveRewardAndNotify(manager.getPlugin(), player, gameData, Timings.endTimings("math-chatgame"));
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
        if (event.getMessage().equals(answer)) {
            win(event.getPlayer());
        }
    }
}
