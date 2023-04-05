package me.epic.chatgames.placeholderapi;

import jdk.jshell.execution.Util;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.epic.chatgames.SimpleChatGames;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.utils.PlayerDataUtils;
import me.epic.chatgames.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleChatGamesExpansion extends PlaceholderExpansion {

    private final GameManager manager;

    public SimpleChatGamesExpansion(GameManager manager) {
        this.manager = manager;
    }


    @Override
    public @NotNull String getIdentifier() {
        return "simplechatgames";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Epic";
    }

    @Override
    public @NotNull String getVersion() {
        return SimpleChatGames.getPlugin().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.startsWith("leaderboard_")) {
            return PlayerDataUtils.getPlayerDataAtPosition(Integer.parseInt(params.replace("leaderboard_", ""))).toString();
        }
        switch (params.toLowerCase()) {
            case "next_game" -> {
                if (manager.isGameRunning()) return "00:00";
                return Utils.formatMillis((manager.getLastGameTime() + manager.getPlugin().getConfig().getInt("games.interval", 5) * 60000L) - System.currentTimeMillis());
            }
            case "wins" -> {
                if (player == null) return "Invalid player";
                return String.valueOf(PlayerDataUtils.getPlayerData(player));
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return List.of("%simplechatgames_leaderboard_<number>%", "%simplechatgames_next_game%", "%simplechatgames_wins%");
    }
}
