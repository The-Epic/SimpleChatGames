package xyz.epicebic.simplechatgames;

import lombok.SneakyThrows;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import me.epic.spigotlib.UpdateChecker;
import me.epic.spigotlib.config.ConfigUpdater;
import me.epic.spigotlib.formatting.Formatting;
import me.epic.spigotlib.language.MessageConfig;
import me.epic.spigotlib.utils.FileUtils;
import me.epic.spigotlib.utils.ServerUtils;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import xyz.epicebic.simplechatgames.commands.CommandHandler;
import xyz.epicebic.simplechatgames.games.GameManager;
import xyz.epicebic.simplechatgames.managers.DataManager;
import xyz.epicebic.simplechatgames.placeholderapi.SimpleChatGamesExpansion;
import xyz.epicebic.simplechatgames.managers.StorageManager;
import xyz.epicebic.simplechatgames.utils.Utils;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public final class SimpleChatGames extends JavaPlugin {

    private static SimpleChatGames plugin;
    private GameManager gameManager;
    private boolean debugMode;
    private MessageConfig messageConfig;
    private YamlConfiguration antiSpamConfig;
    private boolean vaultPresent = false;
    private Economy economy;
    private BukkitTask mainGameTask;

    @Override
    @SneakyThrows
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        loadLibraries();
        saveDefaultConfig(); // TODO change this to load the configs
        StorageManager.getInstance().initData();
        DataManager.getInstance().initRunnable();
        checkForUpdates();
        loadBstats();
        reload();
        ConfigUpdater.update(this, "messages.yml", new File(getDataFolder(), "messages.yml"));
        getCommand("simplechatgames").setExecutor(new CommandHandler(this));
        gameManager = new GameManager(this);
        gameManager.loadGames();

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
        if (gameManager.isGameRunning()) {
            gameManager.getActiveGame().end();
        }
        gameManager.clearActiveGame();
    }

    public void reload() {
        if (mainGameTask != null) {
            mainGameTask.cancel();
        }
        reloadConfig();
        Utils.init();
        debugMode = getConfig().getBoolean("debug", false);
        int delay = 20 * 60 * getConfig().getInt("games.interval");
        AtomicInteger playersNeeded = new AtomicInteger(getConfig().getInt("games.players"));
        if (debugMode) {
            delay = 20 * 20;
            playersNeeded.set(0);
            getLogger().warning("Debug mode enabled");
        }
        mainGameTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if ((debugMode || Bukkit.getOnlinePlayers().size() >= playersNeeded.get())) {
                gameManager.startRandomGame();
            } else {
                if (!gameManager.isGameRunning()) {
                    Bukkit.broadcastMessage(Formatting.translate(getConfig().getString(
                            "games.not-enough-players-message")));
                }
            }
        }, delay, delay);
        FileUtils.loadResourceFile(this, "messages.yml")
                 .ifPresent(file -> this.messageConfig = new MessageConfig(file));
        FileUtils.loadResourceFile(this, "antispam.yml")
                 .ifPresent(file -> this.antiSpamConfig = YamlConfiguration.loadConfiguration(file));
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

    public void loadLibraries() {
        BukkitLibraryManager libraryManager = new BukkitLibraryManager(this);
        Library hikari = Library.builder().artifactId("com.zaxxer").groupId("HikariCP").version("5.0.1").build();
        Library adventureBukkit = Library.builder().artifactId("net.kyori").groupId("adventure-platform-bukkit").version("4.2.0").build();
        Library adventureApi = Library.builder().artifactId("net.kyori").groupId("adventure-api").version("4.13.0").build();
        Library adventureText = Library.builder().artifactId("net.kyori").groupId("adventure-text-minimessage").version("4.13.0").build();
        libraryManager.addMavenCentral();
        libraryManager.loadLibrary(hikari);
        libraryManager.loadLibrary(adventureBukkit);
        libraryManager.loadLibrary(adventureApi);
        libraryManager.loadLibrary(adventureText);
    }

    public void checkForUpdates() {
        new UpdateChecker(this, 108655).runUpdateChecker(getConfig().getInt("update-checker.interval"),
                "https://www.spigotmc.org/resources/simplechatgames.108655/",
                getConfig().getBoolean("update-checker.enabled"));
    }

    public static SimpleChatGames getPlugin() {
        return plugin;
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public MessageConfig getMessageConfig() {
        return this.messageConfig;
    }

    public YamlConfiguration getAntiSpamConfig() {
        return this.antiSpamConfig;
    }

    public boolean isVaultPresent() {
        return this.vaultPresent;
    }

    public Economy getEconomy() {
        return this.economy;
    }
}
