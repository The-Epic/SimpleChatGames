package me.epic.chatgames.commands;

import me.epic.chatgames.SimpleChatGames;
import me.epic.spigotlib.Timings;
import me.epic.spigotlib.commands.SimpleCommandHandler;
import me.epic.spigotlib.formatting.Formatting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends SimpleCommandHandler {

    private final SimpleChatGames plugin;

    public ReloadCommand(SimpleChatGames plugin) {
        super("simplechatgames.command.reload", null);
        this.plugin = plugin;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        Timings.startTimings("plugin-reload");
        plugin.reload();
        plugin.getGameManager().reloadGames();
        String timeTook = Timings.endTimingsString("plugin-reload");
        sender.sendMessage(Formatting.translate("<green>Plugin reloaded in " + timeTook + "ms"));
    }

}
