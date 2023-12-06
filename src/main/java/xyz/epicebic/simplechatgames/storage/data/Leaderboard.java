package xyz.epicebic.simplechatgames.storage.data;

import xyz.epicebic.simplechatgames.configs.GlobalConfig;
import xyz.epicebic.simplechatgames.managers.DataManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Leaderboard {
    private final List<UUID> leaderboard = new ArrayList<>();
    private final DataManager dataManager = DataManager.getInstance();

    public void insert(UUID uuid) {
        this.leaderboard.remove(uuid);
        int searchResult = Collections.binarySearch(this.leaderboard,
                uuid, (uuid1, uuid2) -> Integer.compare(this.dataManager.getPlayerData(uuid1).getWins(),
                        this.dataManager.getPlayerData(uuid2).getWins()));

        int insertIndex = searchResult >= 0 ? searchResult : -(searchResult + 1);
        insertIndex = Math.min(insertIndex, this.leaderboard.size());

        this.leaderboard.add(insertIndex, uuid);
    }

    public List<UUID> getLeaderboard() {
        return new ArrayList<>(this.leaderboard);
    }

    public List<UUID> getLeaderboardPage(int page) {
        int startIndex = (page - 1) * GlobalConfig.LEADERBOARD_ENTRIES_PER_PAGE;
        int endIndex = startIndex + GlobalConfig.LEADERBOARD_ENTRIES_PER_PAGE;

        return this.leaderboard.subList(startIndex, endIndex);
    }
}
