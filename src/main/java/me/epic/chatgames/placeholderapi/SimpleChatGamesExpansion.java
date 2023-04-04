package me.epic.chatgames.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.epic.chatgames.SimpleChatGames;
import me.epic.chatgames.utils.PlayerDataUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleChatGamesExpansion extends PlaceholderExpansion {
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
            default -> {
                return null;
            }
        }
    }
}
