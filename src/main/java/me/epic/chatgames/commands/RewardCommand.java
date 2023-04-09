package me.epic.chatgames.commands;

import me.epic.betteritemconfig.ItemFactory;
import me.epic.chatgames.SimpleChatGames;
import me.epic.spigotlib.commands.SimpleCommandHandler;
import me.epic.spigotlib.formatting.Formatting;
import me.epic.spigotlib.serialisation.ItemSerializer;
import org.apache.commons.lang.WordUtils;
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
                        if (config.getBoolean("rewards.item.enabled")) {
                            ItemFactory.DEFAULT.write(player.getInventory().getItemInMainHand(), plugin.getConfig(), "rewards.item.value");
                            plugin.saveConfig();
                            plugin.reloadConfig();
                            player.sendMessage(Formatting.translate("<green>Item added!"));
                        } else {
                            player.sendMessage("Item rewards are currently disabled.");
                        }
                    }
                    case "command" -> {
                        if (config.getBoolean("rewards.command.enabled")) {
                            StringJoiner joiner = new StringJoiner(" ");
                            for (int i = 2; i < args.length; i++) {
                                joiner.add(args[i]);
                            }
                            config.set("rewards.command.value", joiner.toString());
                            plugin.saveConfig();
                            player.sendMessage(Formatting.translate("<green>Command added!"));
                        } else {
                            player.sendMessage("Command rewards are currently disabled.");
                        }
                    }
                    case "economy" -> {
                        if (config.getBoolean("rewards.economy.enabled")) {
                            double amount = Double.parseDouble(args[2]);
                            config.set("rewards.economy.value", amount);
                            plugin.saveConfig();
                            player.sendMessage(Formatting.translate("<green>Money added!"));
                        } else {
                            player.sendMessage("Economy rewards are currently disabled.");
                        }
                    }
                    default -> player.sendMessage("Invalid command arguments, use /cg reward set <item|command|economy>");
                }
            }
            case "clear" -> {
                for (String reward : config.getConfigurationSection("rewards").getKeys(false)) {
                    if (config.isConfigurationSection("rewards." + reward)) {
                        config.set("rewards." + reward + ".enabled", false);
                        config.set("rewards." + reward + ".value", 0);
                    }
                }
                plugin.saveConfig();
            }
            case "disable" -> {
                if (config.isSet("rewards." + args[1] + ".enabled")) {
                    config.set("rewards." + args[1] + ".enabled", false);
                    config.set("rewards." + args[1] + ".value", 0);
                    plugin.saveConfig();
                    player.sendMessage(Formatting.translate("<green>" + WordUtils.capitalize(args[1]) + " rewards disabled!"));
                } else {
                    player.sendMessage(Formatting.translate("<red>" + WordUtils.capitalize(args[1]) + " rewards not found."));
                }
            }
            default -> player.sendMessage("Invalid command arguments, use /cg reward <set|clear|disable>");
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
