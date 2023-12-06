package xyz.epicebic.simplechatgames.commands;

import xyz.epicebic.simplechatgames.SimpleChatGames;
import me.epic.spigotlib.commands.SimpleCommandHandler;
import org.bukkit.command.CommandSender;

public class SkipCommand extends SimpleCommandHandler {
    
    private final SimpleChatGames plugin;
    
    public SkipCommand(SimpleChatGames plugin) {
        super("simplechatgames.command.skip", null);
        this.plugin = plugin;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] strings) {
        // TODO update to new config
        sender.sendMessage(plugin.getMessageConfig().getString("skip-game"));
        plugin.getGameManager().getActiveGame().end();
        plugin.getGameManager().startRandomGame();
    }

}
