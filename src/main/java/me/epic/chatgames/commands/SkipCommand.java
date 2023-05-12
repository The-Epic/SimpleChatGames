package me.epic.chatgames.commands;

import me.epic.chatgames.SimpleChatGames;
import me.epic.spigotlib.commands.SimpleCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SkipCommand extends SimpleCommandHandler {
    
    private final SimpleChatGames plugin;
    
    public SkipCommand(SimpleChatGames plugin) {
        super("simplechatgames.command.skip", null);
        this.plugin = plugin;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] strings) {
        sender.sendMessage(plugin.getMessageConfig().getString("skip-game"));
        try {
            plugin.getGameManager().getActiveGame().end();
        } catch (NullPointerException ignored) {

        }
        plugin.getGameManager().startRandomGame();
    }

}
