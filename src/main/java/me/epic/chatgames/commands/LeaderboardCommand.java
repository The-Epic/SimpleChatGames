package me.epic.chatgames.commands;

import me.epic.chatgames.SimpleChatGames;
import me.epic.chatgames.utils.PlayerData;
import me.epic.chatgames.utils.PlayerDataUtils;
import me.epic.spigotlib.commands.SimpleCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
public class LeaderboardCommand extends SimpleCommandHandler {

    private final SimpleChatGames plugin;

    public LeaderboardCommand(SimpleChatGames plugin) {
        super("simplechatgames.command.leaderboard", null);
        this.plugin = plugin;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        int page = args.length == 1 ? 1 : Integer.parseInt(args[1]);
        sender.sendMessage(plugin.getMessageConfig().getString("leaderboard.info-message").replace("%number%", String.valueOf(page)));
        int count = 10 * page;
        for (PlayerData data : PlayerDataUtils.getTopPlayerData(count - 10 , count)) {
            sender.sendMessage(plugin.getMessageConfig().getString("leaderboard.info-line").replace("%player_name%", data.getPlayerName()).replace("%wins%", String.valueOf(data.getGamesWon())));
        }
        return;
    }

}
