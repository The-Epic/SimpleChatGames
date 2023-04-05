package me.epic.chatgames.games;

import lombok.Getter;
import me.epic.chatgames.games.data.GameData;
import me.epic.chatgames.utils.PlayerDataUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public abstract class ChatGame<T extends GameData> {
    protected final long startTime;
    @Getter
    private final long endTime;
    protected final GameManager manager;
    protected final GameRunnable timer;
    protected final T gameData;

    public ChatGame(int duration, GameManager manager, T data) {
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

    public void end() {
        end(true);
    }

    public abstract void handleChat(AsyncPlayerChatEvent event);

}