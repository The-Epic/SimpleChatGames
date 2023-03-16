package me.epic.chatgames.games.handlers;

import me.epic.chatgames.Utils;
import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.data.UnscrambleGameData;
import me.epic.spigotlib.Timings;
import me.epic.spigotlib.formatting.Formatting;
import me.epic.spigotlib.utils.SchedulerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UnscrambleGame extends ChatGame {
    /*
    private String answer;
    private final SimpleChatGames plugin = SimpleChatGames.getPlugin();
    private MessageConfig messageConfig = plugin.getMessageConfig();
    private final UnscrambleGameData gameData;

    @Override
    public void runGame() {
        List<String> possibleAnswers = unscrambleConfig.getStringList("words");
        this.answer = possibleAnswers.get(ThreadLocalRandom.current().nextInt(0, possibleAnswers.size()));

        Bukkit.broadcastMessage(messageConfig.getString("unscramble.start").replace("%word%", scrambleWord(answer)));
        Timings.startTimings("unscramble-chatgame");
        this.gameTimer = Bukkit.getScheduler().runTaskLater(plugin, () -> endGame(false, null), 20 * pluginConfig.getInt("game.length"));
        this.midGameMessage = Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.broadcastMessage(messageConfig.getString("unscramble.mid").replace("%time%", String.valueOf(pluginConfig.getInt("game.length") / 2))), 20L * (pluginConfig.getInt("game.length") / 2));
    }



    @Override
    public void endGame(boolean won, Player winner) {
        plugin.getGameManager().endGame();
        if (won) {
            gameTimer.cancel();
            midGameMessage.cancel();
            long timeTookLong = Timings.endTimings("unscramble-chatgame");
            StringBuilder finalTimeTook = new StringBuilder();
            finalTimeTook.append(String.format("%.2f", ((double) timeTookLong / 1000.0)));
            SchedulerUtils.oneTickDelay(plugin, () -> {
                Bukkit.broadcastMessage(messageConfig.getString("unscramble.end.answered").replace("%time%", finalTimeTook.toString()).replace("%player_name%", winner.getName()));
                if (winner.getInventory().addItem(plugin.getRewardItem()).isEmpty()) {
                    winner.sendMessage(messageConfig.getString("all-games.give-reward").replace("%itemname%", plugin.getRewardItem().getItemMeta().getDisplayName()));
                } else {
                    PersistentDataContainer pdc = winner.getPersistentDataContainer();
                    int unclaimedRewards = pdc.getOrDefault(plugin.getUnclaimedRewardKey(), PDT.INTEGER, 0) + 1;
                    pdc.set(plugin.getUnclaimedRewardKey(), PDT.INTEGER, unclaimedRewards);
                    winner.sendMessage(messageConfig.getString("all-games.failed-give-reward").replace("%storedcount%", String.valueOf(unclaimedRewards)));
                }
            });
        } else {
            Bukkit.broadcastMessage(messageConfig.getString("unscramble.end.unanswered").replace("%word%", answer));
        }
        answer = new String();
    }


    public UnscrambleGame(UnscrambleGameData data) {
        super(data.getDuration());
        this.gameData = data;
    }
    private String scrambleWord(String input) {
        String[] words = input.split(" ");
        StringBuilder builder = new StringBuilder(input.length());

        for (String word : words) {
            List<Character> characters = new ArrayList<>(word.chars().mapToObj(i -> (char) i).toList());
            Collections.shuffle(characters);

            for (char character : characters) {
                builder.append(character);
            }
            builder.append(' ');
        }
        return builder.toString().trim();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().equals(answer)) {
            endGame(true, event.getPlayer());
        }
    }

    @Override
    protected void start() {
        List<String> possibleAnswers = gameData.getGameConfig().getStringList("words");
        this.answer = possibleAnswers.get(ThreadLocalRandom.current().nextInt(0, possibleAnswers.size()));

        Bukkit.broadcastMessage(messageConfig.getString("unscramble.start").replace("%word%", scrambleWord(answer)));
        Timings.startTimings("unscramble-chatgame");
    }

    @Override
    protected void end() {

    }

    @Override
    public void handleChat(AsyncPlayerChatEvent event) {
        super.;
    }*/

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
