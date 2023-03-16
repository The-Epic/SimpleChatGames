package me.epic.chatgames.commands;

import me.epic.chatgames.PlayerDataUtils;
import me.epic.chatgames.SimpleChatGames;
import me.epic.spigotlib.commands.SimpleCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LeaderboardCommand extends SimpleCommandHandler {

    private final SimpleChatGames plugin;
    private Map<UUID, Integer> playerWins = new HashMap<>();

    public LeaderboardCommand(SimpleChatGames plugin) {
        super("simplechatgames.command.leaderboard");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        int page = args[0] == null ? 1 : Integer.valueOf(args[0]);
        sender.sendMessage(plugin.getMessageConfig().getString("leaderboard.info-message").replace("%number%", String.valueOf(page)));
        int count = 10 * page;
        for (Map.Entry<String, Integer> entry : PlayerDataUtils.getTopPlayerData(count - 10 , count).entrySet()) {
            sender.sendMessage(plugin.getMessageConfig().getString("leaderboard.info-line").replace("%player_name%", entry.getKey()).replace("%wins%", String.valueOf(entry.getValue())));
        }
        return true;
    }

}
