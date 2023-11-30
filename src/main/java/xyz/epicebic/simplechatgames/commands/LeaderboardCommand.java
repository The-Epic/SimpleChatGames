package xyz.epicebic.simplechatgames.commands;

import xyz.epicebic.simplechatgames.SimpleChatGames;
import xyz.epicebic.simplechatgames.utils.PlayerData;
import xyz.epicebic.simplechatgames.utils.PlayerDataUtils;
import me.epic.spigotlib.commands.SimpleCommandHandler;
import org.bukkit.command.CommandSender;

public class LeaderboardCommand extends SimpleCommandHandler {

    private final SimpleChatGames plugin;

    public LeaderboardCommand(SimpleChatGames plugin) {
        super("simplechatgames.command.leaderboard", null);
        this.plugin = plugin;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        int page = args.length == 1 ? Integer.parseInt(args[0]) : 1;
        sender.sendMessage(plugin.getMessageConfig().getString("leaderboard.info-message").replace("%number%", String.valueOf(page)));
        int count = 10 * page;
        for (PlayerData data : PlayerDataUtils.getTopPlayerData(count - 10 , count)) {
            sender.sendMessage(plugin.getMessageConfig().getString("leaderboard.info-line").replace("%player_name%", data.getPlayerName()).replace("%wins%", String.valueOf(data.getGamesWon())));
        }
        return;
    }

}
