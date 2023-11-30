package xyz.epicebic.simplechatgames.storage;

import com.google.gson.*;
import lombok.SneakyThrows;
import xyz.epicebic.simplechatgames.SimpleChatGames;
import xyz.epicebic.simplechatgames.utils.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class JsonStorageHandler implements StorageHandler {

    private final HashMap<UUID, Integer> playerData = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile = new File(SimpleChatGames.getPlugin().getDataFolder(), "data.json");

    public JsonStorageHandler() {
        if (dataFile.exists()) readFile();
    }


    @Override
    @SneakyThrows
    public int getPlayerData(OfflinePlayer player) {
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

    @Override
    @SneakyThrows
    public void incrementPlayerData(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        int data = getPlayerData(player) + 1;
        playerData.put(uuid, data);
        JsonObject jsonObject = new JsonObject();
        for (UUID playerId : playerData.keySet()) {
            jsonObject.addProperty(playerId.toString(), playerData.get(playerId));
        }
        Files.writeString(dataFile.toPath(), gson.toJson(jsonObject));
    }

    @Override
    public List<PlayerData> getTopPlayerData(int start, int count) {
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

    @Override
    public PlayerData getPlayerDataAtPosition(int position) {
        List<PlayerData> topPlayerData = getTopPlayerData(position - 1, 1);
        if (topPlayerData.isEmpty()) {
            return PlayerData.ofUnknown();
        }
        return topPlayerData.get(0);
    }

    @SneakyThrows
    private void readFile() {
        if (!dataFile.exists()) {
            return;
        }
        String json = Files.readString(dataFile.toPath());
        JsonElement element = new JsonParser().parse(json);
        if (!element.isJsonObject()) {
            return;
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
    }
}
