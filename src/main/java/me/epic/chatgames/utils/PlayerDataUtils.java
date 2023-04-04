package me.epic.chatgames.utils;

import com.google.gson.*;
import lombok.SneakyThrows;
import me.epic.chatgames.SimpleChatGames;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

@SuppressWarnings("deprecation")
public class PlayerDataUtils {

    private static final HashMap<UUID, Integer> playerData = new HashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File dataFile = new File(SimpleChatGames.getPlugin().getDataFolder(), "data.json");

    public static void init() {
        if (dataFile.exists()) {
            readFile();
        }
    }

    @SneakyThrows
    public static int getPlayerData(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        int data = playerData.getOrDefault(uuid, 0);
        if (dataFile.exists()) {
            String json = Files.readString(dataFile.toPath());
            JsonElement element = new JsonParser().parse(json);
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

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public static List<PlayerData> getTopPlayerData(int start, int count) {
        if (!readFile()) {
            return Collections.emptyList();
        }
        List<PlayerData> topPlayerData = new ArrayList<>();
        List<Map.Entry<UUID, Integer>> sortedEntries = new ArrayList<>(playerData.entrySet());
        sortedEntries.sort(Map.Entry.<UUID, Integer>comparingByValue().reversed());

        int endIndex = Math.min(start + count, sortedEntries.size());
        for (int i = start; i < endIndex; i++) {
            Map.Entry<UUID, Integer> entry = sortedEntries.get(i);
            UUID playerId = entry.getKey();
            int gamesWon = entry.getValue();
            topPlayerData.add(new PlayerData(Bukkit.getOfflinePlayer(playerId), gamesWon));
        }
        return topPlayerData;
    }


    public static PlayerData getPlayerDataAtPosition(int position) {
        List<PlayerData> topPlayerData = getTopPlayerData(position - 1, 1);
        if (topPlayerData.isEmpty()) {
            return PlayerData.ofUnknown();
        }
        return topPlayerData.get(0);
    }

    @SneakyThrows
    private static boolean readFile() {
        if (!dataFile.exists()) {
            return false;
        }
        String json = Files.readString(dataFile.toPath());
        JsonElement element = new JsonParser().parse(json);
        if (!element.isJsonObject()) {
            return false;
        }
        JsonObject jsonObject = element.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String playerIdString = entry.getKey();
            JsonElement dataElement = entry.getValue();
            if (dataElement.isJsonPrimitive()) {
                int data = dataElement.getAsInt();
                UUID playerId = UUID.fromString(playerIdString);
                playerData.put(playerId, data);
            }
        }
        return true;
    }
}