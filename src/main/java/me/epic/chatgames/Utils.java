package me.epic.chatgames;

import com.google.common.reflect.TypeToken;
import lombok.SneakyThrows;
import me.epic.spigotlib.config.ConfigUpdater;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Utils {

    @SneakyThrows
    public static Optional<YamlConfiguration> loadResourceFile(Plugin source, String resourceName) {
        System.out.println(source.getDataFolder()+ File.separator + "games");
        File resourceFile = new File(source.getDataFolder() + File.separator + "games", resourceName);

        // Copy file if needed
        if (!resourceFile.exists() && source.getResource("games/" + resourceName) != null) {
            source.saveResource("games/" + resourceName, false);
            ConfigUpdater.update(source, "games/" + resourceName, resourceFile);
        }

        // File still doesn't exist, return empty
        if (!resourceFile.exists()) {
            return Optional.empty();
        }
        return Optional.of(YamlConfiguration.loadConfiguration(resourceFile));
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
}
