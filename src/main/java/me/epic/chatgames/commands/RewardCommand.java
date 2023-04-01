package me.epic.chatgames.commands;

import me.epic.chatgames.SimpleChatGames;
import me.epic.spigotlib.commands.SimpleCommandHandler;
import me.epic.spigotlib.serialisation.ItemSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class RewardCommand extends SimpleCommandHandler {

    private final SimpleChatGames plugin;

    public RewardCommand(SimpleChatGames plugin) {
        super("simplechatgames.command.reward");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        FileConfiguration config = plugin.getConfig();
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is for players only!");
            return true;
         }
        switch (args[0]) {
            case "set" -> {
                switch (args[1]) {
                    case "item" -> {
                        config.set("rewards.item", ItemSerializer.toBase64(player.getInventory().getItemInMainHand()));
                        plugin.saveConfig();
                        plugin.reloadConfig();
                    }
                    case "command" -> {
                        StringJoiner joiner = new StringJoiner(" ");
                        for (int i = 2; i < args.length; i++) {
                            joiner.add(args[i]);
                        }
                        config.set("rewards.command", joiner.toString());
                        plugin.saveConfig();
                    }
                    case "economy" -> {
                        config.set("rewards.economy", Double.valueOf(args[2]));
                        plugin.saveConfig();
                    }
                    default -> player.sendMessage("Invalid command arguments, use /cg reward set <item|command|economy>");
                }
            }
            case "clear" -> {
                for (String reward : config.getConfigurationSection("rewards").getKeys(false)) {
                    config.set(reward, "disabled");
                }
                plugin.saveConfig();
            }
            case "disable" -> {
                config.set("rewards." + args[1], "disabled");
                plugin.saveConfig();
            }
            default -> player.sendMessage("Invalid command arguments, use /cg reward <set|clear|disabled>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        switch (args.length) {
            case 2 -> {
                return StringUtil.copyPartialMatches(args[0], List.of("set", "clear", "disable"), new ArrayList<>());
            }
            case 3 -> {
                switch (args[0]) {
                    case "set", "disable" -> {
                        return StringUtil.copyPartialMatches(args[1], List.of("economy", "item", "command"), new ArrayList<>());
                    }
                    default -> {
                        return Collections.emptyList();
                    }
                }
            }
            default -> {
                return Collections.emptyList();
            }
        }
    }
}
