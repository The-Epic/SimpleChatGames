package me.epic.chatgames;

import com.google.gson.*;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

@SuppressWarnings("deprecation")
public class PlayerDataUtils {

    private static final HashMap<UUID, Integer> playerData = new HashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser jsonParser = new JsonParser();
    private static final File dataFile = new File(SimpleChatGames.getPlugin().getDataFolder(), "data.json");

    @SneakyThrows
    public static int getPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        int data = playerData.getOrDefault(uuid, 0);
        if (dataFile.exists()) {
            String json = Files.readString(dataFile.toPath());
            JsonElement element = jsonParser.parse(json);
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                if (jsonObject.has(uuid.toString())) {
                    data = jsonObject.get(uuid.toString()).getAsInt();
                }
            }
        }
        playerData.put(uuid, data);
        return data;
    }

    @SneakyThrows
    public static void incrementPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        int data = getPlayerData(player) + 1;
        playerData.put(uuid, data);
        JsonObject jsonObject = new JsonObject();
        for (UUID playerId : playerData.keySet()) {
            jsonObject.addProperty(playerId.toString(), playerData.get(playerId));
        }
        Files.writeString(dataFile.toPath(), gson.toJson(jsonObject));
    }

    @SneakyThrows
    public static Map<String, Integer> getTopPlayerData(int start, int count) {
        if (!dataFile.exists()) {
            return Collections.emptyMap();
        }
        String json = Files.readString(dataFile.toPath());
        JsonElement element = jsonParser.parse(json);
        if (!element.isJsonObject()) {
            return Collections.emptyMap();
        }
        JsonObject jsonObject = element.getAsJsonObject();
        Map<UUID, Integer> playerData = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String playerIdString = entry.getKey();
            JsonElement dataElement = entry.getValue();
            if (dataElement.isJsonPrimitive()) {
                int data = dataElement.getAsInt();
                UUID playerId = UUID.fromString(playerIdString);
                playerData.put(playerId, data);
            }
        }
        Map<UUID, String> playerNames = new HashMap<>();
        for (UUID uuid : playerData.keySet()) {
            playerNames.put(uuid, Bukkit.getOfflinePlayer(uuid).getName());
        }
        Map<String, Integer> topPlayerData = new LinkedHashMap<>();
        playerData.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .skip(start)
                .limit(count)
                .forEach(entry -> {
                    UUID playerId = entry.getKey();
                    String playerName = playerNames.getOrDefault(playerId, "Unknown");
                    topPlayerData.put(playerName, entry.getValue());
                });
        return topPlayerData;
    }
}