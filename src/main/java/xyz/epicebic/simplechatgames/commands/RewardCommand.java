package xyz.epicebic.simplechatgames.commands;

import me.epic.betteritemconfig.ItemFactory;
import xyz.epicebic.simplechatgames.SimpleChatGames;
import xyz.epicebic.simplechatgames.utils.Utils;
import me.epic.spigotlib.commands.SimpleCommandHandler;
import me.epic.spigotlib.formatting.Formatting;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class RewardCommand extends SimpleCommandHandler {

    private final SimpleChatGames plugin;

    public RewardCommand(SimpleChatGames plugin) {
        super("simplechatgames.command.reward", null);
        this.plugin = plugin;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        FileConfiguration config = plugin.getConfig();
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is for players only!");
            return;
        }
        // TODO message config, update to new item stuff
        switch (args[0]) {
            case "set" -> {
                switch (args[1]) {
                    case "item" -> {
                        ItemFactory.DEFAULT.write(player.getInventory().getItemInMainHand(), plugin.getConfig(), "rewards.item.value");
                        plugin.saveConfig();
                        plugin.reloadConfig();
                        if (config.getBoolean("rewards.item.enabled")) {
                            player.sendMessage(Formatting.translate("<green>Item added!"));
                        } else {
                            player.sendMessage(Formatting.translate("<green>Item added! <red><bold>[</bold>!<bold>]</bold> This reward is currently disabled, run \"/cg reward enable item\" to enable it"));
                        }
                        Utils.init();
                    }
                    case "command" -> {
                        List<String> currentCommands = config.getStringList("rewards.command.value");
                        StringJoiner joiner = new StringJoiner(" ");
                        for (int i = 2; i < args.length; i++) {
                            joiner.add(args[i]);
                        }
                        currentCommands.add(joiner.toString());
                        config.set("rewards.command.value", currentCommands);
                        plugin.saveConfig();
                        if (config.getBoolean("rewards.command.enabled")) {
                            player.sendMessage(Formatting.translate("<green>Command added!"));
                        } else {
                            player.sendMessage(Formatting.translate("<green>Item added! <red><bold>[</bold>!<bold>]</bold> This reward is currently disabled, run \"/cg reward enable command\" to enable it"));
                        }
                    }
                    case "economy" -> {
                        double amount = Double.parseDouble(args[2]);
                        config.set("rewards.economy.value", amount);
                        plugin.saveConfig();
                        if (config.getBoolean("rewards.economy.enabled")) {
                            player.sendMessage(Formatting.translate("<green>Money added!"));
                        } else {
                            player.sendMessage(Formatting.translate("<green>Item added! <red><bold>[</bold>!<bold>]</bold> This reward is currently disabled, run \"/cg reward enable economy\" to enable it"));
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
                    plugin.saveConfig();
                    plugin.reloadConfig();
                    player.sendMessage(Formatting.translate("<green>" + WordUtils.capitalize(args[1]) + " rewards disabled!"));
                } else {
                    player.sendMessage(Formatting.translate("<red>" + WordUtils.capitalize(args[1]) + " rewards not found."));
                }
            }
            case "enable" -> {
                if (args[1].equalsIgnoreCase("item") || args[1].equalsIgnoreCase("economy") || args[1].equalsIgnoreCase("command")) {
                    config.set("rewards." + args[1] + ".enabled", true);
                    plugin.saveConfig();
                    plugin.reloadConfig();
                    player.sendMessage(Formatting.translate("<green>" + WordUtils.capitalize(args[1]) + " rewards enabled!"));
                }
            }
            default -> player.sendMessage("Invalid command arguments, use /cg reward <set|clear|disable>");
        }
        return;
    }

    @Override
    public List<String> handleTabCompletion(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return StringUtil.copyPartialMatches(args[0], List.of("set", "clear", "disable", "enable"), new ArrayList<>());
            }
            case 2 -> {
                switch (args[0]) {
                    case "set", "disable", "enable" -> {
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
