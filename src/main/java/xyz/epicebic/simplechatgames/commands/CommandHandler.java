package xyz.epicebic.simplechatgames.commands;

import xyz.epicebic.simplechatgames.SimpleChatGames;
import me.epic.spigotlib.commands.ArgumentCommandHandler;

public class CommandHandler extends ArgumentCommandHandler {

    public CommandHandler(SimpleChatGames plugin) {
        super(plugin.getMessageConfig(), "simplechatgames.command", null);

        LeaderboardCommand leaderboardCommand = new LeaderboardCommand(plugin);
        SkipCommand skipCommand = new SkipCommand(plugin);
        addArgumentExecutor("leaderboard", leaderboardCommand);
        addArgumentExecutor("lb", leaderboardCommand);
        addArgumentExecutor("reload", new ReloadCommand(plugin));
        addArgumentExecutor("skip", skipCommand);
        addArgumentExecutor("next", skipCommand);
        addArgumentExecutor("reward", new RewardCommand(plugin));

    }
}
