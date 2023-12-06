package xyz.epicebic.simplechatgames.commands;

import xyz.epicebic.simplechatgames.SimpleChatGames;
import me.epic.spigotlib.commands.SimpleCommandHandler;
import org.bukkit.command.CommandSender;
import xyz.epicebic.simplechatgames.managers.DataManager;
import xyz.epicebic.simplechatgames.managers.StorageManager;

import java.util.*;

public class LeaderboardCommand extends SimpleCommandHandler {

    private final SimpleChatGames plugin;

    public LeaderboardCommand(SimpleChatGames plugin) {
        super("simplechatgames.command.leaderboard", null);
        this.plugin = plugin;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        int page = args.length == 1 ? Integer.parseInt(args[0]) : 1;
        // TODO update to new config
//        sender.sendMessage(plugin.getMessageConfig().getString("leaderboard.info-message").replace("%number%", String.valueOf(page)));
        List<String> messages = new ArrayList<>();
        Map<String, Integer> nameWinsMap = new HashMap<>();
        for (UUID uuid : DataManager.getInstance().getLeaderboard(page)) {
            nameWinsMap.put(DataManager.getInstance().getPlayerData(uuid).getName(), DataManager.getInstance().getPlayerData(uuid).getWins());
        }
        // TODO send message
    }

}
