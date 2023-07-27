package me.epic.chatgames;

import lombok.Getter;
import lombok.SneakyThrows;
import me.epic.betteritemconfig.ItemFactory;
import me.epic.chatgames.commands.CommandHandler;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.placeholderapi.SimpleChatGamesExpansion;
import me.epic.chatgames.utils.PlayerDataUtils;
import me.epic.chatgames.utils.Utils;
import me.epic.spigotlib.UpdateChecker;
import me.epic.spigotlib.config.ConfigUpdater;
import me.epic.spigotlib.formatting.Formatting;
import me.epic.spigotlib.language.MessageConfig;
import me.epic.spigotlib.serialisation.ItemSerializer;
import me.epic.spigotlib.utils.FileUtils;
import me.epic.spigotlib.utils.ServerUtils;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class SimpleChatGames extends JavaPlugin {

    @Getter private static SimpleChatGames plugin;
    @Getter private GameManager gameManager;
    @Getter private boolean debugMode;
    @Getter private MessageConfig messageConfig;
    @Getter private YamlConfiguration antiSpamConfig;
    @Getter private boolean vaultPresent = false;
    @Getter private Economy economy;
    private BukkitTask mainGameTask;


    @Override
    @SneakyThrows
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        saveDefaultConfig();
        new UpdateChecker(this, 108655).runUpdateChecker(getConfig().getInt("update-checker.interval"), "https://www.spigotmc.org/resources/simplechatgames.108655/", getConfig().getBoolean("update-checker.enabled"));
        loadBstats();
        reload();
        ConfigUpdater.update(this, "messages.yml", new File(getDataFolder(), "messages.yml"));
        getCommand("simplechatgames").setExecutor(new CommandHandler(this));
        gameManager = new GameManager(this);
        gameManager.loadGames();
        PlayerDataUtils.init(getConfig().getString("storage.type", "json"));

        updateConfig();

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            getLogger().info("Vault found, registering compatibility.");
            vaultPresent = true;
            setupEconomy();
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("PlaceholderAPI found, registering compatibility.");
            new SimpleChatGamesExpansion(gameManager).register();
        }
    }

    @Override
    public void onDisable() {
        if (gameManager.isGameRunning()) gameManager.getActiveGame().end();
        gameManager.clearActiveGame();
    }

    public void reload() {
        if (mainGameTask != null) mainGameTask.cancel();
        reloadConfig();
        Utils.init();
        debugMode = getConfig().getBoolean("debug", false);
        int delay = 20 * 60 * getConfig().getInt("games.interval");
        int interval = delay;
        AtomicInteger playersNeeded = new AtomicInteger(getConfig().getInt("games.players"));
        if (debugMode) {
            delay = 20 * 20;
            interval = 20 * 60;
            playersNeeded.set(0);
            getLogger().warning("Debug mode enabled");
        }
        mainGameTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if ((debugMode || Bukkit.getOnlinePlayers().size() >= playersNeeded.get())) {
                gameManager.startRandomGame();
            } else {
                if (!gameManager.isGameRunning())
                    Bukkit.broadcastMessage(Formatting.translate(getConfig().getString("games.not-enough-players-message")));
            }
        }, delay, interval);
        FileUtils.loadResourceFile(this, "messages.yml").ifPresent(file -> this.messageConfig = new MessageConfig(file));
        FileUtils.loadResourceFile(this, "antispam.yml").ifPresent(file -> this.antiSpamConfig = YamlConfiguration.loadConfiguration(file));
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
    }

    public void loadBstats() {
        Metrics metrics = new Metrics(this, 17979);
        metrics.addCustomChart(new SimplePie("online_mode", ServerUtils::getMode));
    }

    //The jankiest way for me to not update config updater
    public void updateConfig() {
        try {
            // Rewards updating
            ConfigurationSection rewards = getConfig().getConfigurationSection("rewards");
            assert rewards != null;
            if (!rewards.isConfigurationSection("command") && rewards.getString("command", "disabled").equals("disabled")) {
                rewards.set("command", "");
                rewards.set("command.enabled", false);
                rewards.set("command.value", "nocommand");
            } else if (!rewards.isConfigurationSection("command")) {
                String oldCommand = rewards.getString("command");
                rewards.set("command", "");
                rewards.set("command.enabled", true);
                rewards.set("command.value", oldCommand);
            }
            if (rewards.isString("economy") && rewards.getString("economy").equals("disabled")) {
                rewards.set("economy", "");
                rewards.set("economy.enabled", false);
                rewards.set("economy.value", 0);
            } else if (!rewards.isConfigurationSection("economy")){
                double oldValue = rewards.getDouble("economy");
                rewards.set("economy", "");
                rewards.set("economy.enabled", true);
                rewards.set("economy.value", oldValue);
            }
            if (!rewards.isConfigurationSection("item") && rewards.getString("item", "disabled").equals("disabled")) {
                rewards.set("item", "");
                rewards.set("item.enabled", false);
                rewards.set("item.value", "");
            } else if (!rewards.isConfigurationSection("item")) {
                String b64Item = rewards.getString("item");
                rewards.set("item", "");
                rewards.set("item.enabled", true);
                ItemStack item = ItemSerializer.itemStackFromBase64(b64Item);
                ItemFactory.DEFAULT.write(item, getConfig(), "rewards.item.value");
            }

            // Rewards p2 -- commands being a list instead
            if (rewards.isString("command.value") || rewards.isInt("command.value")) {
                List<String> commandRewards = new ArrayList<>();
                if (rewards.isString("command.value")) commandRewards.add(rewards.getString("command.value"));
                rewards.set("command.value", commandRewards);
            }

            // Add sounds info
            ConfigurationSection gamesSection = getConfig().getConfigurationSection("games");
            if (!gamesSection.isSet("sounds")) {
                List<String> comments = List.of("Valid sounds can be found on the following link", "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html");
                gamesSection.set("sounds.enabled", false);
                gamesSection.set("sounds.start", "BLOCK_ANVIL_LAND");
                gamesSection.set("sounds.win", "ENTITY_VILLAGER_YES");
                gamesSection.setComments("sounds", comments);
            }
        } finally {
            saveConfig();
        }
    }
}
