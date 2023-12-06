package xyz.epicebic.simplechatgames.managers;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.epicebic.simplechatgames.SimpleChatGames;
import xyz.epicebic.simplechatgames.storage.data.Leaderboard;
import xyz.epicebic.simplechatgames.storage.data.PlayerData;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private static final DataManager INSTANCE = new DataManager();

    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private final Leaderboard leaderboard = new Leaderboard();

    private final Map<UUID, Instant> removalQueue = new HashMap<>();

    public void initRunnable() {
        new DataRemovalRunnable().runTaskTimer(SimpleChatGames.getPlugin(), 0, 20 * 60);
    }

    public PlayerData getPlayerData(UUID uuid) {
        return this.playerDataMap.get(uuid);
    }

    public void loadPlayerData(UUID uuid) {
        if (this.playerDataMap.containsKey(uuid)) {
            return;
        }
        StorageManager.getInstance().loadPlayerData(uuid);
    }

    public void store(UUID uuid, PlayerData playerData) {
        this.playerDataMap.put(uuid, playerData);
        this.leaderboard.insert(uuid);
    }

    public void unloadQueue(UUID uuid) {
        this.removalQueue.put(uuid, Instant.now());
    }

    public List<UUID> getLeaderboard(int page) {
        return this.leaderboard.getLeaderboardPage(page);
    }

    public static DataManager getInstance() {
        return INSTANCE;
    }

    private class DataRemovalRunnable extends BukkitRunnable {
        @Override
        public void run() {
            for (Map.Entry<UUID, Instant> entry : removalQueue.entrySet()) {
                Instant now = Instant.now();
                if (entry.getValue().plus(Duration.ofMinutes(15))
                         .isAfter(now) && Bukkit.getPlayer(entry.getKey()) == null) {
                    playerDataMap.remove(entry.getKey());
                    removalQueue.remove(entry.getKey());

                }
            }
        }
    }
}
