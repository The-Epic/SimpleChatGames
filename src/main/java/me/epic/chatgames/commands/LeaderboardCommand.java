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
        for (Map.Entry<String, Integer> entry : PlayerDataUtils.getTopPlayerData(0, 9).entrySet()) {
            sender.sendMessage("  -" + entry.getKey() + "  " + entry.getValue());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

}
