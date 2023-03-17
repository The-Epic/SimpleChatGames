package me.epic.chatgames.utils;

import me.epic.chatgames.SimpleChatGames;
import me.epic.chatgames.games.data.GameData;
import me.epic.spigotlib.config.ConfigUpdater;
import me.epic.spigotlib.formatting.Formatting;
import me.epic.spigotlib.serialisation.ItemSerializer;
import me.epic.spigotlib.utils.SchedulerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

public class Utils {

    public static Optional<File> loadResourceFile(Plugin source, String resourceName, String folder) {
        File resourceFile = new File(source.getDataFolder() + folder, resourceName);

        // Copy file if needed
        if (!resourceFile.exists() && source.getResource(resourceName) != null) {
            source.saveResource(resourceName, false);
            ConfigUpdater.update(source, resourceName, resourceFile);
        }

        // File still doesn't exist, return empty
        if (!resourceFile.exists()) {
            return Optional.empty();
        }
        return Optional.of(resourceFile);
    }

    public static Optional<YamlConfiguration> loadResource(Plugin source, String resourceName, String folderName) {
        Optional<File> optional = loadResourceFile(source, resourceName, folderName);

        if (optional.isPresent()) {
            return Optional.of(YamlConfiguration.loadConfiguration(optional.get()));
        } else {
            return Optional.empty();
        }
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

    public static void giveRewardAndNotify(SimpleChatGames plugin, Player player, GameData gameData, String timeTook) {
        FileConfiguration config = plugin.getConfig();
        SchedulerUtils.oneTickDelay(plugin, () -> {
            Bukkit.broadcastMessage(Formatting.translate(gameData.getGameConfig().getString("messages.end.won").replace("%time%", timeTook.toString()).replace("%player_name%", player.getName())));
            if (plugin.isVaultPresent() && !(config.get("rewards.economy") instanceof String)) {
                plugin.getEconomy().depositPlayer(player, config.getDouble("rewards.economy"));
                player.sendMessage(plugin.getMessageConfig().getString("money-given").replace("%amount%", config.getString("rewards.economy")));
            }
            if (!config.getString("rewards.command").equals("disabled")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), config.getString("rewards.command").replace("%player_name%", player.getName()));
            }
            if (!config.getString("rewards.item").equals("disabled")) {
                ItemStack itemStack = ItemSerializer.itemStackFromBase64(config.getString("rewards.item"));
                player.getInventory().addItem(itemStack);
                player.sendMessage(plugin.getMessageConfig().getString("item-given").replace("%item_count%", String.valueOf(itemStack.getAmount())).replace("%item_name%", itemStack.getItemMeta().getDisplayName()));
            }
        });

    }

    public static String formatListAnswers(List<String> answers) {
        StringJoiner joiner = new StringJoiner(", ");
        for (String string : answers) {
            joiner.add(string);
        }
        return joiner.toString();
    }
}
