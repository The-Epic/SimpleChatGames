package me.epic.chatgames.utils;

import lombok.SneakyThrows;
import me.epic.betteritemconfig.ItemFactory;
import me.epic.chatgames.SimpleChatGames;
import me.epic.chatgames.games.data.GameData;
import me.epic.spigotlib.config.ConfigUpdater;
import me.epic.spigotlib.formatting.Formatting;
import me.epic.spigotlib.material.MaterialUtils;
import me.epic.spigotlib.serialisation.ItemSerializer;
import me.epic.spigotlib.utils.SchedulerUtils;
import me.epic.spigotlib.utils.StringUtils;
import me.epic.spigotlib.utils.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utils {
    private static boolean ran = false;

    public static Optional<File> loadResourceFile(Plugin source, String resourceName) {
        File resourceFile = new File(source.getDataFolder() + File.separator + "games", resourceName);

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

    public static Optional<YamlConfiguration> loadResource(JavaPlugin source, String resourceName) {
        if (!ran) {
            loadFiles(source, file -> source.saveResource(file, false));
            ran = true;
        }
        Optional<File> optional = loadResourceFile(source, resourceName);

        return optional.map(YamlConfiguration::loadConfiguration);
    }

    @SneakyThrows
    private static void loadFiles(JavaPlugin plugin, Consumer<String> consumer) {
        final File jarFile = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        if (jarFile.isFile()) {
            final JarFile jar = new JarFile(jarFile);
            final Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) continue;

                final String name = entry.getName();
                if (name.startsWith("games" + "/")) {
                    File file = new File(plugin.getDataFolder(), name);
                    if (plugin.getDataFolder().exists() && !(file.exists())) {
                        consumer.accept(name);
                    }
                    if (file.exists()) ConfigUpdater.update(plugin, name, file);
                }
            }
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
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.broadcastMessage(Formatting.translate(gameData.getGameConfig().getString("messages.end.won").replace("%time%", timeTook.toString()).replace("%player_name%", player.getName())));
            if (config.getBoolean("rewards.economy.enabled") && plugin.isVaultPresent()) {
                double economyReward = config.getDouble("rewards.economy.value");
                plugin.getEconomy().depositPlayer(player, economyReward);
                player.sendMessage(plugin.getMessageConfig().getString("money-given").replace("%amount%", String.valueOf(economyReward)));
            }
            if (config.getBoolean("rewards.command.enabled")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), config.getString("rewards.command.value").replace("%player_name%", player.getName()));
            }
            if (config.getBoolean("rewards.item.enabled")) {
                ItemStack itemStack = ItemFactory.DEFAULT.read(config.getConfigurationSection("rewards.item.value"));
                player.getInventory().addItem(itemStack);
                player.sendMessage(plugin.getMessageConfig().getString("item-given").replace("%item_count%", String.valueOf(itemStack.getAmount())).replace("%item_name%", itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : WordUtils.getNiceName(itemStack.getType().toString())));
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
}
