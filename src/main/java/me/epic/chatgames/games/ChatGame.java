package me.epic.chatgames.games;

import me.epic.chatgames.PlayerDataUtils;
import me.epic.chatgames.games.data.GameData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public abstract class ChatGame<T extends GameData> {
    protected final long startTime;
    protected final long endTime;
    protected final GameManager manager;
    protected final GameRunnable timer;
    protected final T gameData;

    protected ChatGame(int duration, GameManager manager, T data) {
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + (duration * 1000);
        this.gameData = data;

        this.manager = manager;
        this.timer = new GameRunnable(this);
    }

    protected void start() {
        timer.runTaskTimer(manager.getPlugin(), 0, 1);
    }

    protected void win(Player player) {
        end(false);
        PlayerDataUtils.incrementPlayerData(player);
    }

    protected void end(boolean timeout) {
        manager.clearActiveGame();
        timer.cancel();
    }

    public abstract void handleChat(AsyncPlayerChatEvent event);

}