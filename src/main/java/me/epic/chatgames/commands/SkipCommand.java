package me.epic.chatgames.commands;

import me.epic.chatgames.SimpleChatGames;
import me.epic.spigotlib.commands.SimpleCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SkipCommand extends SimpleCommandHandler {
    
    private final SimpleChatGames plugin;
    
    public SkipCommand(SimpleChatGames plugin) {
        super("simplechatgames.command.skip");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        sender.sendMessage(plugin.getMessageConfig().getString("skip-game"));
        try {
            plugin.getGameManager().getActiveGame().end();
        } catch (NullPointerException ignored) {

        }
        plugin.getGameManager().startRandomGame();
        return true;
    }
}
