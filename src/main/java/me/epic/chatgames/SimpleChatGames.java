package me.epic.chatgames;

import lombok.Getter;
import me.epic.betteritemconfig.ItemFactory;
import me.epic.chatgames.commands.CommandHandler;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.placeholderapi.SimpleChatGamesExpansion;
import me.epic.chatgames.utils.PlayerDataUtils;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public final class SimpleChatGames extends JavaPlugin {

    @Getter private static SimpleChatGames plugin;
    @Getter private GameManager gameManager;
    @Getter private boolean debugMode;
    @Getter private MessageConfig messageConfig;
    @Getter private boolean vaultPresent = false;
    @Getter private Economy economy;
    private BukkitTask mainGameTask;


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        new UpdateChecker(this, 108655).runUpdateChecker(getConfig().getInt("update-checker.interval"), "https://www.spigotmc.org/resources/simplechatgames.108655/", getConfig().getBoolean("update-checker.enabled"));
        loadBstats();
        reload();
        ConfigUpdater.update(this, "messages.yml", new File(getDataFolder(), "messages.yml"));
        getCommand("simplechatgames").setExecutor(new CommandHandler(this));
        gameManager = new GameManager(this);
        gameManager.loadGames();
        PlayerDataUtils.init(getConfig().getString("storage.type", "json"));

        if (!getConfig().getString("item", "disabled").equals("disable")) {
            ItemStack toConvert = ItemSerializer.itemStackFromBase64(getConfig().getString("rewards.item"));
            ItemFactory.DEFAULT.write(toConvert, getConfig(), "rewards.item");
            saveConfig();
            reloadConfig();
        }
        ConfigUpdater.runConfigUpdater(this);


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

}
