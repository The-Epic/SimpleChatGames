package me.epic.chatgames.commands;

import me.epic.chatgames.SimpleChatGames;
import me.epic.spigotlib.commands.ArgumentCommandHandler;
import me.epic.spigotlib.language.MessageConfig;

public class CommandHandler extends ArgumentCommandHandler {

    private final SimpleChatGames plugin;

    public CommandHandler(SimpleChatGames plugin) {
        super(plugin.getMessageConfig(), "simplechatgames.command");
        this.plugin = plugin;

        LeaderboardCommand leaderboardCommand = new LeaderboardCommand(plugin);
        addArgumentExecutor("leaderboard", leaderboardCommand);
        addArgumentExecutor("lb", leaderboardCommand);

    }
}
