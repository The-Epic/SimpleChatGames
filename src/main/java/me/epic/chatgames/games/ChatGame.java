package me.epic.chatgames.games;

import lombok.Getter;
import me.epic.chatgames.games.data.GameData;
import me.epic.chatgames.utils.PlayerDataUtils;
import me.epic.chatgames.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public abstract class ChatGame<T extends GameData> {
    protected final long startTime;
    @Getter
    private final long endTime;
    protected final GameManager manager;
    protected final GameRunnable timer;
    protected final T gameData;
    private Sound startSound;
    private Sound winSound;

    public ChatGame(int duration, GameManager manager, T data) {
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + (duration * 1000);
        this.gameData = data;

        this.manager = manager;
        this.timer = new GameRunnable(this);

        if (manager.getPlugin().getConfig().getBoolean("games.sounds.enabled", false)) {
            this.startSound = Sound.valueOf(manager.getPlugin().getConfig().getString("games.sounds.start", "BLOCK_ANVIL_FALL").toUpperCase());
            this.winSound = Sound.valueOf(manager.getPlugin().getConfig().getString("games.sounds.win", "ENTITY_VILLAGER_CELEBRATE").toUpperCase());
        }
    }

    protected void start() {
        timer.runTaskTimer(manager.getPlugin(), 0, 1);

        if (startSound != null) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.playSound(onlinePlayer.getLocation(), startSound, SoundCategory.AMBIENT, 1f, 1f);
            }
        }
    }

    protected void win(Player player) {
        end(false);
        PlayerDataUtils.incrementPlayerData(player);
        if (winSound != null) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.playSound(onlinePlayer.getLocation(), winSound, SoundCategory.AMBIENT, 1f, 1f);
            }
        }
    }

    protected void end(boolean timeout) {
        manager.clearActiveGame();
        timer.cancel();
    }

    public void end() {
        end(true);
    }

    public abstract void handleChat(AsyncPlayerChatEvent event);

    protected void sendDebugAnswer(String answer) {
        if (manager.getPlugin().isDebugMode()) Bukkit.getOperators().forEach(offlinePlayer -> {
            if (offlinePlayer.isOnline()) {
                Bukkit.getPlayer(offlinePlayer.getName()).sendMessage("Chat Game Answer: " + answer);
            }
        });
    }

}