package me.epic.chatgames;

import lombok.SneakyThrows;
import me.epic.spigotlib.config.ConfigUpdater;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Utils {

    @SneakyThrows
    public static Optional<YamlConfiguration> loadResourceFile(Plugin source, String resourceName) {
        File resourceFile = new File(source.getDataFolder().getCanonicalPath() + "/games", resourceName);

        // Copy file if needed
        if (!resourceFile.exists() && source.getResource(resourceName) != null) {
            source.saveResource(resourceName, false);
            ConfigUpdater.update(source, resourceName, resourceFile);
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
