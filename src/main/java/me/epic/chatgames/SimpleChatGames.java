package me.epic.chatgames;

import lombok.Getter;
import me.epic.chatgames.commands.CommandHandler;
import me.epic.chatgames.games.GameManager;
import me.epic.spigotlib.UpdateChecker;
import me.epic.spigotlib.config.ConfigUpdater;
import me.epic.spigotlib.formatting.Formatting;
import me.epic.spigotlib.items.ItemBuilder;
import me.epic.spigotlib.language.MessageConfig;
import me.epic.spigotlib.utils.FileUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public final class SimpleChatGames extends JavaPlugin {

    @Getter private static SimpleChatGames plugin;
    @Getter private GameManager gameManager;
    @Getter private ItemStack rewardItem;
    @Getter private boolean debugMode;
    @Getter private MessageConfig messageConfig;
    @Getter private boolean vaultPresent = false;
    @Getter private Economy economy;
    private BukkitTask mainGameTask;


    @Override
    public void onEnable() {
        // Plugin startup logic
        new UpdateChecker(this, 0).runUpdateChecker(getConfig().getInt("update-checker.interval"), "cool link", getConfig().getBoolean("update-checker.enabled"));
        ConfigUpdater.runConfigUpdater(this);
        reload();
        ConfigUpdater.update(this, "messages.yml", new File(getDataFolder(), "messages.yml"));
        getCommand("simplechatgames").setExecutor(new CommandHandler(this));
        plugin = this;
        gameManager = new GameManager(this);
        gameManager.loadGames();
        rewardItem = new ItemBuilder(Material.SUNFLOWER).name("<#1efb41>C<#22fb4a>h<#26fb52>a<#2afc5b>t <#2efc64>G<#32fc6c>a<#36fc75>m<#3afc7e>e <#3efc86>T<#42fd8f>o<#46fd98>k<#4afda0>e<#4efda9>n").enchantment(Enchantment.MENDING, 1).flags(ItemFlag.HIDE_ENCHANTS).build();

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            System.out.println("test");
            vaultPresent = true;
            setupEconomy();
        }
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

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

}
