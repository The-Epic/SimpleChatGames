package xyz.epicebic.simplechatgames.utils;

import me.epic.betteritemconfig.ItemFactory;
import xyz.epicebic.simplechatgames.SimpleChatGames;
import xyz.epicebic.simplechatgames.games.data.GameData;
import me.epic.spigotlib.formatting.Formatting;
import me.epic.spigotlib.utils.RandomUtils;
import me.epic.spigotlib.utils.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Utils {
    private static ItemStack rewardStack;

    public static void init() {
        SimpleChatGames plugin = SimpleChatGames.getPlugin();
        if (plugin.getConfig().getBoolean("rewards.item.enabled") && !plugin.getConfig().getString("rewards.item.value", "empty").equals("empty")) {
            rewardStack = ItemFactory.DEFAULT.read(plugin.getConfig().getConfigurationSection("rewards.item.value"));
        }
    }

    public static Optional<File> loadResourceFile(Plugin source, String resourceName, File parentFolder) {
        File resourceFile = new File(parentFolder, resourceName);

        // Copy file if needed
        if (!resourceFile.exists()) {
            source.saveResource("games/" + resourceName, false);
        }

        // File still doesn't exist, return empty
        if (!resourceFile.exists()) {
            return Optional.empty();
        }
        return Optional.of(resourceFile);
    }


    public static String scrambleWord(String input) {
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

    public static void giveRewardAndNotify(SimpleChatGames plugin, Player player, GameData gameData, long timingsResult) {
        String timeTook = String.format("%.2f", ((double) timingsResult / 1000.0));
        FileConfiguration config = plugin.getConfig();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.broadcastMessage(Formatting.translate(gameData.getGameConfig().getString("messages.end.won").replace("%time%", timeTook.toString()).replace("%player_name%", player.getName())));
            if (config.getBoolean("rewards.economy.enabled") && plugin.isVaultPresent()) {
                double economyReward = config.getDouble("rewards.economy.value");
                plugin.getEconomy().depositPlayer(player, economyReward);
                player.sendMessage(plugin.getMessageConfig().getString("money-given").replace("%amount%", String.valueOf(economyReward)));
            }
            if (config.getBoolean("rewards.command.enabled")&& !plugin.getConfig().getString("rewards.item.value", "empty").equals("empty")) {
                List<String> commands = config.getStringList("rewards.command.value");
                commands.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player_name%", player.getName())));
            }
            if (config.getBoolean("rewards.item.enabled")) {
                player.getInventory().addItem(rewardStack);
                player.sendMessage(plugin.getMessageConfig().getString("item-given").replace("%item_count%", String.valueOf(rewardStack.getAmount())).replace("%item_name%", rewardStack.getItemMeta().hasDisplayName() ? rewardStack.getItemMeta().getDisplayName() : WordUtils.getNiceName(rewardStack.getType().toString())));
            }
        }, 5);
    }

    public static String formatListAnswers(List<String> answers) {
        StringJoiner joiner = new StringJoiner(", ");
        for (String string : answers) {
            joiner.add(string);
        }
        return joiner.toString();
    }

    public static String formatMillis(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static List<Map<String, List<String>>> fixList(List<Map<?, ?>> originalList) {
        List<Map<String, List<String>>> convertedList = new ArrayList<>();

        for (Map<?, ?> originalMap : originalList) {
            Map<String, List<String>> convertedMap = new HashMap<>();

            for (Map.Entry<?, ?> entry : originalMap.entrySet()) {
                String key = entry.getKey().toString();
                List<String> value = entry.getValue() instanceof List<?> ? (List<String>) entry.getValue() : Collections.singletonList(entry.getValue().toString());

                convertedMap.put(key, value);
            }

            convertedList.add(convertedMap);
        }

        return convertedList;
    }

    public static File updateQuestions(File gameFile) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(gameFile);
        if (config.isConfigurationSection("questions")) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            ConfigurationSection questionsSection = config.getConfigurationSection("questions");

            for (String questionKey : questionsSection.getKeys(false)) {
                ConfigurationSection questionSection = questionsSection.getConfigurationSection(questionKey);
                List<String> answers = questionSection.getStringList("answers");

                Map<String, Object> questionMap = new LinkedHashMap<>();
                questionMap.put("question", questionKey);
                questionMap.put("answers", answers);

                mapList.add(questionMap);
            }

            config.set("questions", mapList);

            System.out.println(SimpleChatGames.getPlugin().getDataFolder());
            try {
                config.save(gameFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return gameFile;
    }

    public static String addBlanks(String fullWord, double chance) {
        List<String> words = List.of(fullWord.split(" "));
        StringJoiner returnedWords = new StringJoiner(" ");
        for (String word : words) {
            StringBuilder updatedWord = new StringBuilder();
            char[] characters = word.toCharArray();
            for (int i = 0; i < characters.length; i++) {
                if (i != 0 && RandomUtils.chance(chance)) {
                    updatedWord.append("_");
                    continue;
                }
                updatedWord.append(characters[i]);
            }
            returnedWords.add(updatedWord);
        }
        return returnedWords.toString().trim();
    }
}
